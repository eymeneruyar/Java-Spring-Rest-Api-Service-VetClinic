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
import vetcilinicservice.Documents.ElasticBuying;
import vetcilinicservice.Entities.*;
import vetcilinicservice.Repositories._elastic.ElasticBuyingRepository;
import vetcilinicservice.Repositories._jpa.BuyingPaymentRepository;
import vetcilinicservice.Repositories._jpa.BuyingRepository;
import vetcilinicservice.Repositories._jpa.SupplierProductRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class BuyingLayer {

    final BuyingRepository buyingRepo;
    final SupplierProductRepository supProRepo;
    final BuyingPaymentRepository buyingPaymentRepository;
    final ElasticBuyingRepository ebRepo;
    final ElasticsearchOperations operations;
    public BuyingLayer(BuyingRepository buyingRepo, SupplierProductRepository supProRepo, BuyingPaymentRepository buyingPaymentRepository, ElasticBuyingRepository ebRepo, ElasticsearchOperations operations) {
        this.buyingRepo = buyingRepo;
        this.supProRepo = supProRepo;
        this.buyingPaymentRepository = buyingPaymentRepository;
        this.ebRepo = ebRepo;
        this.operations = operations;
    }

    //Buying Add
    public Map<ERest,Object> add(Buying buying, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Buying buy = buyingRepo.save(buying);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Alış ekleme işlemi başarılı!");
                hm.put(ERest.result,buy);
                //Buying Payment Actions - Start
                BuyingPayment buyingPayment = new BuyingPayment();
                buyingPayment.setBuying(buy);

                buyingPayment.setTotalDebt(Integer.parseInt(buying.getBuyAmount()) * buy.getSupplierProduct().getSupProPrice());
                buyingPayment.setRemainDebt(Integer.parseInt(buying.getBuyAmount()) * buy.getSupplierProduct().getSupProPrice());

                buyingPaymentRepository.save(buyingPayment);
                //Buying Payment Actions - End

                //Elasticsearch save
                ElasticBuying eb = new ElasticBuying();
                eb.setBuyId(buy.getBuyId());
                eb.setProductName(buy.getSupplierProduct().getSupProName());
                eb.setSupplierName(buy.getSupplier().getSupName());
                ebRepo.save(eb);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Alış işlemi sırasında bir hata oluştu!";
                Util.logger(error, Buying.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //Buying List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Buying> buyingList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            buyingList = buyingRepo.findByOrderByBuyIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki alış listeleme işlemi başarılı!");
            hm.put(ERest.result,buyingList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Buying.class);
        }
        return hm;
    }

    //Product list with supplier ID
    public Map<ERest,Object> listWithSupId(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<SupplierProduct> supplierProductList = new ArrayList<>();
        try {
            int id = Integer.parseInt(stId);
            supplierProductList = supProRepo.findBySupplier_SupIdEquals(id);
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Seçilen tedarikçinin ürün listeleme işlemi başarılı!");
            hm.put(ERest.result,supplierProductList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,SupplierProduct.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - Start -------------------------------------//

    public Map<ERest,Object> elasticSearch(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla arama kriteri eklemek için multiMatchQuery yapısı kullanılır.
                .withQuery(multiMatchQuery(data,"supplierName")
                        .field("productName")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticBuying>> list = operations.search(query,ElasticBuying.class).getSearchHits();
        if(list.size() > 0 ){
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Arama işlemi başarılı!");
            hm.put(ERest.result,list);
        }else{
            hm.put(ERest.status,false);
            hm.put(ERest.message,"Arama kriterlerinize uygun sonuç bulunamadı!");
        }
        return hm;
    }

    public Map<ERest,Object> elasticInsertData(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Buying> buyingList = buyingRepo.findAll();
        try {
            if(buyingList.size() > 0){
                buyingList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticBuying eb = new ElasticBuying();
                    eb.setBuyId(item.getBuyId());
                    eb.setProductName(item.getSupplierProduct().getSupProName());
                    eb.setSupplierName(item.getSupplier().getSupName());
                    ebRepo.save(eb);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,ebRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı alış bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Customer.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Buying.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
