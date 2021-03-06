package vetcilinicservice.Dto;

import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vetcilinicservice.Documents.ElasticSales;
import vetcilinicservice.Entities.*;
import vetcilinicservice.Repositories._elastic.ElasticSalesRepository;
import vetcilinicservice.Repositories._jpa.*;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class SalesLayer {

    final SalesRepository salesRepo;
    final CustomerRepository cuRepo;
    final VaccineRepository vacRepo;
    final ProductRepository proRepo;
    final PatientRepository paRepo;
    final SalesPaymentRepository salesPaymentRepository;
    final ElasticSalesRepository esRepo;
    final ElasticsearchOperations operations;
    public SalesLayer(SalesRepository salesRepo, CustomerRepository cuRepo, VaccineRepository vacRepo, ProductRepository proRepo, PatientRepository paRepo, SalesPaymentRepository salesPaymentRepository, ElasticSalesRepository esRepo, ElasticsearchOperations operations) {
        this.salesRepo = salesRepo;
        this.cuRepo = cuRepo;
        this.vacRepo = vacRepo;
        this.proRepo = proRepo;
        this.paRepo = paRepo;
        this.salesPaymentRepository = salesPaymentRepository;
        this.esRepo = esRepo;
        this.operations = operations;
    }

    //Sales Add
    public Map<ERest,Object> add(Sales sales, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Sales sal = salesRepo.save(sales);
                //??r??n ve a???? stok g??ncelleme, ??denecek tutar hesaplama - Start
                salesRepo.procProductUpdateStock(sal.getProduct().getProId(),Integer.parseInt(sal.getSaPrAmount()));
                SalesPayment salesPayment = new SalesPayment();
                salesPayment.setSales(sal);
                Customer customer = cuRepo.findById(sales.getCustomer().getCuId()).get();
                sal.setCustomer(customer);
                Vaccine vaccine = vacRepo.findById(sales.getVaccine().getVacid()).get();
                sal.setVaccine(vaccine);
                Product product = proRepo.findById(sales.getProduct().getProId()).get();
                sal.setProduct(product);
                Patient patient = paRepo.findById(sales.getPatient().getPaId()).get();
                sal.setPatient(patient);
                salesPayment.setTotalDebt(Integer.parseInt(sal.getSaPrAmount()) * product.getProSalesPrice() + Integer.parseInt(sal.getSaVacAmount())*vaccine.getVacSalesPrice());
                salesPayment.setRemainDebt(Integer.parseInt(sal.getSaPrAmount()) * product.getProSalesPrice() + Integer.parseInt(sal.getSaVacAmount())*vaccine.getVacSalesPrice());
                salesPaymentRepository.save(salesPayment);
                //??r??n ve a???? stok g??ncelleme, ??denecek tutar hesaplama - End
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Sat???? i??lemi ba??ar??l??!");
                hm.put(ERest.result,sal);
                //Elasticsearch save
                ElasticSales es = new ElasticSales();
                es.setSaId(sal.getSaId());
                es.setName(sal.getCustomer().getCuName());
                es.setInvoice(sal.getSaReceiptNo());
                esRepo.save(es);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Sat???? i??lemi s??ras??nda bir hata olu??tu!";
                Util.logger(error, Sales.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //Sales List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Sales> salesList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            salesList = salesRepo.findByOrderBySaIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki sat???? listeleme i??lemi ba??ar??l??!");
            hm.put(ERest.result,salesList);
        } catch (Exception e) {
            String error = "Listeleme s??ras??nda bir hata olu??tu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Sales.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - Start -------------------------------------//

    public Map<ERest,Object> elasticSearch(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla aram kriteri eklemek i??in multiMatchQuery yap??s?? kullan??l??r.
                .withQuery(multiMatchQuery(data,"name")
                        .field("invoice")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticSales>> list = operations.search(query,ElasticSales.class).getSearchHits();
        if(list.size() > 0 ){
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Arama i??lemi ba??ar??l??!");
            hm.put(ERest.result,list);
        }else{
            hm.put(ERest.status,false);
            hm.put(ERest.message,"Arama kriterlerinize uygun sonu?? bulunamad??!");
        }
        return hm;
    }

    public Map<ERest,Object> elasticInsertData(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Sales> salesList = salesRepo.findAll();
        try {
            if(salesList.size() > 0){
                salesList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticSales es = new ElasticSales();
                    es.setSaId(item.getSaId());
                    es.setName(item.getCustomer().getCuName());
                    es.setInvoice(item.getSaReceiptNo());
                    esRepo.save(es);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme i??lemi ba??ar??l??!");
                hm.put(ERest.result,esRepo.findAll());
            }else {
                String error = "Sisteme kay??tl?? sat???? bulunmamaktad??r!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Sales.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri taban??na ekleme yap??l??rken bir hata olu??tu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Sales.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
