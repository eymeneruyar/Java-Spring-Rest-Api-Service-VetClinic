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
import vetcilinicservice.Documents.ElasticVaccine;
import vetcilinicservice.Entities.Vaccine;
import vetcilinicservice.Repositories._elastic.ElasticVaccineRepository;
import vetcilinicservice.Repositories._jpa.VaccineRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class VaccineDefinitionLayer {

    final VaccineRepository vacRepo;
    final ElasticVaccineRepository evRepo;
    final ElasticsearchOperations operations;
    public VaccineDefinitionLayer(VaccineRepository vacRepo, ElasticVaccineRepository evRepo, ElasticsearchOperations operations) {
        this.vacRepo = vacRepo;
        this.evRepo = evRepo;
        this.operations = operations;
    }

    //Vaccine Add
    public Map<ERest,Object> add(Vaccine vaccine, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Vaccine vac = vacRepo.save(vaccine);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Aşı ekleme işlemi başarılı!");
                hm.put(ERest.result,vac);
                //Elasticsearch save
                ElasticVaccine ev = new ElasticVaccine();
                ev.setVacid(vac.getVacid());
                ev.setName(vac.getVacName());
                ev.setBarcode(vac.getVacBarcode());
                ev.setCategory(vac.getVacCategory());
                evRepo.save(ev);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Aşı ekleme sırasında bir hata oluştu!" + e;
                Util.logger(error, Vaccine.class);
                hm.put(ERest.message,error);

            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All Vaccine List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Vaccine> vaccineList = new ArrayList<>();
        try {
            vaccineList = vacRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Aşı listeleme işlemi başarılı!");
            hm.put(ERest.result,vaccineList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Vaccine.class);
        }
        return hm;
    }

    //Vaccine List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Vaccine> vaccineList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            vaccineList = vacRepo.findByOrderByVacidAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki aşı listeleme işlemi başarılı!");
            hm.put(ERest.result,vaccineList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Vaccine.class);
        }
        return hm;
    }

    //Vaccine delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Vaccine> optVaccine = vacRepo.findById(id);
            if(optVaccine.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticVaccine ev = evRepo.findById(id).get();
                vacRepo.deleteById(id);
                evRepo.deleteById(ev.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optVaccine.get());
            }else {
                String error = "Silmek istenen aşı bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Vaccine.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Vaccine.class);
        }
        return hm;
    }

    //Vaccine update
    public Map<ERest,Object> update(Vaccine vaccine,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(vaccine.getVacid() != null && !bindingResult.hasErrors()){
            Optional<Vaccine> optVaccine = vacRepo.findById(vaccine.getVacid());
            if(optVaccine.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticVaccine ev = evRepo.findById(vaccine.getVacid()).get();
                    evRepo.deleteById(ev.getId());
                    Vaccine vac = vacRepo.saveAndFlush(vaccine);
                    ElasticVaccine evNew = new ElasticVaccine();
                    evNew.setVacid(vac.getVacid());
                    evNew.setName(vac.getVacName());
                    evNew.setBarcode(vac.getVacBarcode());
                    evNew.setCategory(vac.getVacCategory());
                    evRepo.save(evNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,vac);
                } catch (Exception e) {
                    String error = "Güncelleme işlemi sırasında bir hata oluştu! " + e + " ";
                    hm.put(ERest.status,false);
                    hm.put(ERest.message,error);
                    hm.put(ERest.result,vaccine);
                    Util.logger(error,Vaccine.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak aşı bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,vaccine);
                Util.logger(error,Vaccine.class);
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
                        .field("category")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticVaccine>> list = operations.search(query,ElasticVaccine.class).getSearchHits();
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
        List<Vaccine> vaccineList = vacRepo.findAll();
        try {
            if(vaccineList.size() > 0){
                vaccineList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticVaccine ev = new ElasticVaccine();
                    ev.setVacid(item.getVacid());
                    ev.setName(item.getVacName());
                    ev.setBarcode(item.getVacBarcode());
                    ev.setCategory(item.getVacCategory());
                    evRepo.save(ev);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,evRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı aşı bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Vaccine.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Vaccine.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
