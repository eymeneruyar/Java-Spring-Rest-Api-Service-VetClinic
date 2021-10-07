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
import vetcilinicservice.Documents.ElasticSupplier;
import vetcilinicservice.Entities.Supplier;
import vetcilinicservice.Repositories._elastic.ElasticSupplierRepository;
import vetcilinicservice.Repositories._jpa.SupplierRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class SupplierLayer {

    final SupplierRepository supRepo;
    final ElasticSupplierRepository esupRepo;
    final ElasticsearchOperations operations;
    public SupplierLayer(SupplierRepository supRepo, ElasticSupplierRepository esupRepo, ElasticsearchOperations operations) {
        this.supRepo = supRepo;
        this.esupRepo = esupRepo;
        this.operations = operations;
    }

    //Supplier Add
    public Map<ERest,Object> add(Supplier supplier, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Supplier sup = supRepo.save(supplier);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Tedarikçi ekleme işlemi başarılı!");
                hm.put(ERest.result,sup);
                //Elasticsearch save
                ElasticSupplier es = new ElasticSupplier();
                es.setSupId(sup.getSupId());
                es.setEmail(sup.getSupEmail());
                es.setName(sup.getSupName());
                es.setPhone(sup.getSupPhone());
                esupRepo.save(es);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                if(e.toString().contains("constraint")){
                    String error = "Bu e-mail adresi, telefon ya da isim ile daha önce kayıt yapılmış!";
                    Util.logger(error, Supplier.class);
                    hm.put(ERest.message,error);
                }
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All Supplier List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Supplier> supplierList = new ArrayList<>();
        try {
            supplierList = supRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Tedarikçi listeleme işlemi başarılı!");
            hm.put(ERest.result,supplierList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Supplier.class);
        }
        return hm;
    }

    //Supplier List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Supplier> supplierList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            supplierList = supRepo.findByOrderBySupIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki tedarikçi listeleme işlemi başarılı!");
            hm.put(ERest.result,supplierList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Supplier.class);
        }
        return hm;
    }

    //Supplier delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Supplier> optSupplier = supRepo.findById(id);
            if(optSupplier.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticSupplier es = esupRepo.findById(id).get();
                supRepo.deleteById(id);
                esupRepo.deleteById(es.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optSupplier.get());
            }else {
                String error = "Silmek istenen tedarikçi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Supplier.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Supplier.class);
        }
        return hm;
    }

    //Supplier update
    public Map<ERest,Object> update(Supplier supplier,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(supplier.getSupId() != null && !bindingResult.hasErrors()){
            Optional<Supplier> optSupplier = supRepo.findById(supplier.getSupId());
            if(optSupplier.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticSupplier es = esupRepo.findById(supplier.getSupId()).get();
                    esupRepo.deleteById(es.getId());
                    Supplier sup = supRepo.saveAndFlush(supplier);
                    ElasticSupplier esNew = new ElasticSupplier();
                    esNew.setSupId(sup.getSupId());
                    esNew.setEmail(sup.getSupEmail());
                    esNew.setName(sup.getSupName());
                    esNew.setPhone(sup.getSupPhone());
                    esupRepo.save(esNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,sup);
                } catch (Exception e) {
                    hm.put(ERest.status,false);
                    if(e.toString().contains("constraint")){
                        String error = "Bu e-mail adresi, telefon ya da isim ile daha önce kayıt yapılmış!";
                        Util.logger(error, Supplier.class);
                        hm.put(ERest.message,error);
                    }
                }
            }else{
                String error = "Güncelleme işlemi yapılacak tedarikçi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,supplier);
                Util.logger(error,Supplier.class);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.message,Util.errors(bindingResult));
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - Start -------------------------------------//

    public Map<ERest,Object> elasticSearch(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla aram kriteri eklemek için multiMatchQuery yapısı kullanılır.
                .withQuery(multiMatchQuery(data,"name")
                        .field("email")
                        .field("phone")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticSupplier>> list = operations.search(query,ElasticSupplier.class).getSearchHits();
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
        List<Supplier> supplierList = supRepo.findAll();
        try {
            if(supplierList.size() > 0){
                supplierList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticSupplier es = new ElasticSupplier();
                    es.setSupId(item.getSupId());
                    es.setEmail(item.getSupEmail());
                    es.setName(item.getSupName());
                    es.setPhone(item.getSupPhone());
                    esupRepo.save(es);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,esupRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı müşteri bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Supplier.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Supplier.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
