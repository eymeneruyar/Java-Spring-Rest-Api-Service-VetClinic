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
import vetcilinicservice.Documents.ElasticPatient;
import vetcilinicservice.Entities.Patient;
import vetcilinicservice.Repositories._elastic.ElasticPatientRepository;
import vetcilinicservice.Repositories._jpa.PatientRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class PatientLayer {

    final PatientRepository paRepo;
    final ElasticPatientRepository epaRepo;
    final ElasticsearchOperations operations;
    public PatientLayer(PatientRepository paRepo, ElasticPatientRepository epaRepo, ElasticsearchOperations operations) {
        this.paRepo = paRepo;
        this.epaRepo = epaRepo;
        this.operations = operations;
    }

    //Patient Add
    public Map<ERest,Object> add(Patient patient,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Patient pa = paRepo.save(patient);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Hasta ekleme işlemi başarılı!");
                hm.put(ERest.result,pa);
                //Elasticsearch save
                ElasticPatient ep = new ElasticPatient();
                ep.setPaId(pa.getPaId());
                ep.setName(pa.getPaName());
                ep.setChipNo(pa.getPaChipNo());
                epaRepo.save(ep);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                if(e.toString().contains("constraint")){
                    String error = "Bu çip numarası ("+patient.getPaChipNo()+") ile daha önce kayıt yapılmış!";
                    Util.logger(error, Patient.class);
                    hm.put(ERest.message,error);
                }
            }
        }else{
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //Patient List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Patient> patientList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            patientList = paRepo.findByOrderByPaIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki hasta listeleme işlemi başarılı!");
            hm.put(ERest.result,patientList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Patient.class);
        }
        return hm;
    }

    //Patient list with customer ID
    public Map<ERest,Object> listWithCuId(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Patient> patientList = new ArrayList<>();
        try {
            int id = Integer.parseInt(stId);
            patientList = paRepo.findByCustomer_CuIdEquals(id);
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Hasta listeleme işlemi başarılı!");
            hm.put(ERest.result,patientList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Patient.class);
        }
        return hm;
    }

    //Patient delete -> Bağlı olduğu tablolar olduğu için silme aksiyonu işlemiyor.
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Patient> optPatient = paRepo.findById(id);
            if(optPatient.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticPatient ep = epaRepo.findById(id).get();
                paRepo.deleteById(id);
                epaRepo.deleteById(ep.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optPatient.get());
            }else {
                String error = "Silmek istenen hasta bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Patient.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Patient.class);
        }
        return hm;
    }

    //Patient update
    public Map<ERest,Object> update(Patient patient, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(patient.getPaId() != null && !bindingResult.hasErrors()){
            Optional<Patient> optPatient = paRepo.findById(patient.getPaId());
            if(optPatient.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticPatient ep = epaRepo.findById(patient.getPaId()).get();
                    epaRepo.deleteById(ep.getId());
                    Patient pa = paRepo.saveAndFlush(patient);
                    ElasticPatient epNew = new ElasticPatient();
                    epNew.setPaId(pa.getPaId());
                    epNew.setName(pa.getPaName());
                    epNew.setChipNo(pa.getPaChipNo());
                    epaRepo.save(epNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,pa);
                } catch (Exception e) {
                    String error = "Güncelleme işlemi sırasında bir hata oluştu! " + e + " ";
                    hm.put(ERest.status,false);
                    if(e.toString().contains("constraint")){
                        error += "Bu çip numarası ("+patient.getPaChipNo()+") ile daha önce kayıt yapılmış";
                        hm.put(ERest.message,error);
                    }
                    hm.put(ERest.result,patient);
                    Util.logger(error,Patient.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak müşteri bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,patient);
                Util.logger(error,Patient.class);
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
                        .field("chipNo")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticPatient>> list = operations.search(query,ElasticPatient.class).getSearchHits();
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
        List<Patient> patientList = paRepo.findAll();
        try {
            if(patientList.size() > 0){
                patientList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticPatient ep = new ElasticPatient();
                    ep.setPaId(item.getPaId());
                    ep.setName(item.getPaName());
                    ep.setChipNo(item.getPaChipNo());
                    epaRepo.save(ep);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,epaRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı hasta bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Patient.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Patient.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
