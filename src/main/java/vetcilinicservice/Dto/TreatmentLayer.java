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
import vetcilinicservice.Documents.ElasticCustomer;
import vetcilinicservice.Documents.ElasticTreatment;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Treatment;
import vetcilinicservice.Repositories._elastic.ElasticTreatmentRepository;
import vetcilinicservice.Repositories._jpa.TreatmentRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class TreatmentLayer {

    final TreatmentRepository treRepo;
    final ElasticTreatmentRepository etRepo;
    final ElasticsearchOperations operations;
    public TreatmentLayer(TreatmentRepository treRepo, ElasticTreatmentRepository etRepo, ElasticsearchOperations operations) {
        this.treRepo = treRepo;
        this.etRepo = etRepo;
        this.operations = operations;
    }

    //Treatment add
    public Map<ERest,Object> add(Treatment treatment, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Treatment tre = treRepo.save(treatment);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Muayene ekleme işlemi başarılı!");
                hm.put(ERest.result,tre);
                //Elasticsearch save
                ElasticTreatment et = new ElasticTreatment();
                et.setTreId(tre.getTreId());
                et.setTrecode(tre.getTreCode());
                et.setPname(tre.getPatient().getPaName());
                et.setCname(tre.getCustomer().getCuName());
                et.setTrenote(tre.getTreNote());
                etRepo.save(et);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Muayene ekleme sırasında bir hata oluştu!" + e;
                Util.logger(error, Treatment.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //Treatment List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Treatment> treatmentList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            treatmentList = treRepo.findByOrderByTreIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki muayene listeleme işlemi başarılı!");
            hm.put(ERest.result,treatmentList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Treatment.class);
        }
        return hm;
    }

    //Treatment delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Treatment> optTreatment = treRepo.findById(id);
            if(optTreatment.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticTreatment et = etRepo.findById(id).get();
                treRepo.deleteById(id);
                etRepo.deleteById(et.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optTreatment.get());
            }else {
                String error = "Silmek istenen muayene bilgisi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Treatment.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Treatment.class);
        }
        return hm;
    }

    //Treatment update
    public Map<ERest,Object> update(Treatment treatment,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(treatment.getTreId() != null && !bindingResult.hasErrors()){
            Optional<Treatment> optTreatment = treRepo.findById(treatment.getTreId());
            if(optTreatment.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticTreatment et = etRepo.findById(treatment.getTreId()).get();
                    etRepo.deleteById(et.getId());
                    Treatment tre = treRepo.saveAndFlush(treatment);
                    ElasticTreatment etNew = new ElasticTreatment();
                    etNew.setTreId(tre.getTreId());
                    etNew.setTrecode(tre.getTreCode());
                    etNew.setPname(tre.getPatient().getPaName());
                    etNew.setCname(tre.getCustomer().getCuName());
                    etNew.setTrenote(tre.getTreNote());
                    etRepo.save(etNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,et);
                } catch (Exception e) {
                    String error = "Muayene güncelleme işlemi sırasında bir hata oluştu! " + e ;
                    hm.put(ERest.status,false);
                    hm.put(ERest.message,error);
                    hm.put(ERest.result,treatment);
                    Util.logger(error,Treatment.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak muayene bilgisi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,treatment);
                Util.logger(error,Treatment.class);
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
                .withQuery(multiMatchQuery(data,"cname")
                        .field("pname")
                        .field("trenote")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticTreatment>> list = operations.search(query, ElasticTreatment.class).getSearchHits();
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
        List<Treatment> treatmentList = treRepo.findAll();
        try {
            if(treatmentList.size() > 0){
                treatmentList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticTreatment et = new ElasticTreatment();
                    et.setTreId(item.getTreId());
                    et.setCname(item.getCustomer().getCuName());
                    et.setPname(item.getPatient().getPaName());
                    et.setTrecode(item.getTreCode());
                    et.setTrenote(item.getTreNote());
                    etRepo.save(et);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,etRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı müşteri bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Treatment.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Treatment.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
