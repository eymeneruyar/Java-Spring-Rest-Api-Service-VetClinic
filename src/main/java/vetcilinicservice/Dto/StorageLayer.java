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
import vetcilinicservice.Documents.ElasticStorage;
import vetcilinicservice.Entities.Storage;
import vetcilinicservice.Repositories._elastic.ElasticStorageRepository;
import vetcilinicservice.Repositories._jpa.StorageRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class StorageLayer {

    final StorageRepository sRepo;
    final ElasticStorageRepository esRepo;
    final ElasticsearchOperations operations;
    public StorageLayer(StorageRepository sRepo, ElasticStorageRepository esRepo, ElasticsearchOperations operations) {
        this.sRepo = sRepo;
        this.esRepo = esRepo;
        this.operations = operations;
    }

    //Storage Add
    public Map<ERest,Object> add(Storage storage, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Storage stor = sRepo.save(storage);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Depo ekleme işlemi başarılı!");
                hm.put(ERest.result,stor);
                //Elasticsearch save
                ElasticStorage es = new ElasticStorage();
                es.setStorId(stor.getStorId());
                es.setName(stor.getStorName());
                es.setNo(stor.getStorNo());
                es.setStatus(stor.getStorStatus());
                esRepo.save(es);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Depo ekleme işlemi sırasında bir hata oluştu!";
                Util.logger(error, Storage.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All Storage List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Storage> storageList = new ArrayList<>();
        try {
            storageList = sRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Depo listeleme işlemi başarılı!");
            hm.put(ERest.result,storageList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Storage.class);
        }
        return hm;
    }

    //Storage List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Storage> storageList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            storageList = sRepo.findByOrderByStorIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki depo listeleme işlemi başarılı!");
            hm.put(ERest.result,storageList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Storage.class);
        }
        return hm;
    }

    //Customer delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Storage> optStorage = sRepo.findById(id);
            if(optStorage.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticStorage es = esRepo.findById(id).get();
                sRepo.deleteById(id);
                esRepo.deleteById(es.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optStorage.get());
            }else {
                String error = "Silmek istenen depo bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Storage.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Storage.class);
        }
        return hm;
    }

    //Storage update
    public Map<ERest,Object> update(Storage storage,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(storage.getStorId() != null && !bindingResult.hasErrors()){
            Optional<Storage> optStorage = sRepo.findById(storage.getStorId());
            if(optStorage.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticStorage es = esRepo.findById(storage.getStorId()).get();
                    esRepo.deleteById(es.getId());
                    Storage stor = sRepo.saveAndFlush(storage);
                    ElasticStorage esNew = new ElasticStorage();
                    esNew.setStorId(stor.getStorId());
                    esNew.setName(stor.getStorName());
                    esNew.setNo(stor.getStorNo());
                    esNew.setStatus(stor.getStorStatus());
                    esRepo.save(esNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,stor);
                } catch (Exception e) {
                    hm.put(ERest.status,false);
                    String error = "Depo ekleme işlemi sırasında bir hata oluştu!";
                    Util.logger(error, Storage.class);
                    hm.put(ERest.message,error);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak müşteri bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,storage);
                Util.logger(error,Storage.class);
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
                        .field("no")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticStorage>> list = operations.search(query,ElasticStorage.class).getSearchHits();
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
        List<Storage> storageList = sRepo.findAll();
        try {
            if(storageList.size() > 0){
                storageList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticStorage es = new ElasticStorage();
                    es.setStorId(item.getStorId());
                    es.setName(item.getStorName());
                    es.setNo(item.getStorNo());
                    es.setStatus(item.getStorStatus());
                    esRepo.save(es);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,esRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı depo bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Storage.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Storage.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
