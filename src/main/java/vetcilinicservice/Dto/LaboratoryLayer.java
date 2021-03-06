package vetcilinicservice.Dto;

import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import vetcilinicservice.Documents.ElasticCustomer;
import vetcilinicservice.Documents.ElasticLaboratory;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Laboratory;
import vetcilinicservice.Entities.Patient;
import vetcilinicservice.Repositories._elastic.ElasticLaboratoryRepository;
import vetcilinicservice.Repositories._jpa.CustomerRepository;
import vetcilinicservice.Repositories._jpa.LaboratoryRepository;
import vetcilinicservice.Repositories._jpa.PatientRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class LaboratoryLayer {

    final LaboratoryRepository labRepo;
    final CustomerRepository cuRepo;
    final PatientRepository paRepo;
    final ElasticLaboratoryRepository elRepo;
    final ElasticsearchOperations operations;
    public LaboratoryLayer(LaboratoryRepository labRepo, CustomerRepository cuRepo, PatientRepository paRepo, ElasticLaboratoryRepository elRepo, ElasticsearchOperations operations) {
        this.labRepo = labRepo;
        this.cuRepo = cuRepo;
        this.paRepo = paRepo;
        this.elRepo = elRepo;
        this.operations = operations;
    }

    //Laboratory Add
    public Map<ERest,Object> add(MultipartFile file,Laboratory laboratory, BindingResult bindingResult){
        long maxFileUploadSize = 2048;
        int sendSuccessCount = 0;
        String errorMessage = "";
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            if (!file.isEmpty() ){
                long fileSizeMB = file.getSize() / 1024;
                if ( fileSizeMB > maxFileUploadSize ) {
                    System.err.println("Dosya boyutu ??ok b??y??k Max 2MB");
                    errorMessage = "Dosya boyutu ??ok b??y??k Max "+ (maxFileUploadSize / 1024) +"MB olmal??d??r";
                }else {
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    String ext = fileName.substring(fileName.length()-5, fileName.length());
                    String uui = UUID.randomUUID().toString();
                    fileName = uui + ext;
                    try {
                        Path path = Paths.get(Util.UPLOAD_DIR + fileName);
                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                        sendSuccessCount += 1;
                        laboratory.setLabFileName(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Customer customer = cuRepo.findById(laboratory.getLabCuId()).get();
                    Patient patient = paRepo.findById(laboratory.getLabPaId()).get();
                    laboratory.setCustomer(customer);
                    laboratory.setPatient(patient);
                    Laboratory lab = labRepo.save(laboratory);
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Laboratuvar sonucu ekleme i??lemi ba??ar??l??!");
                    hm.put(ERest.result,lab);
                    //Elasticsearch save
                    ElasticLaboratory el = new ElasticLaboratory();
                    el.setLabId(lab.getLabId());
                    el.setCuname(lab.getCustomer().getCuName());
                    el.setPaname(lab.getPatient().getPaName());
                    el.setPaAirTagNo(lab.getPatient().getPaAirTagNo());
                    elRepo.save(el);
                } catch (Exception e) {
                    hm.put(ERest.status,false);
                    String error = "Laboratuvar sonucu eklerken bir hata olu??tu!";
                    Util.logger(error, Laboratory.class);
                    hm.put(ERest.message,error);
                }
            }else{
                String error = "L??tfen bir dosya se??iniz!";
                hm.put(ERest.status,false);
                Util.logger(error, Laboratory.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //Laboratory List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Laboratory> laboratoryList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            laboratoryList = labRepo.findByOrderByLabIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki laboratuvar sonucu listeleme i??lemi ba??ar??l??!");
            hm.put(ERest.result,laboratoryList);
        } catch (Exception e) {
            String error = "Listeleme s??ras??nda bir hata olu??tu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Laboratory.class);
        }
        return hm;
    }

    //Laboratory result detail
    public Map<ERest,Object> detail(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Laboratory> optDetail = labRepo.findById(id);
            if(optDetail.isPresent()){
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Laboratuvar sonucu detay?? ba??ar??l?? bir ??ekilde getirildi.");
                hm.put(ERest.result,optDetail.get());
            }else{
                String error = "Laboratuvar sonucu bulunamad??!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Laboratory.class);
            }
        } catch (Exception e) {
            String error = "Laboratuvar sonu?? detay?? getirilirken bir hata olu??tu!";
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Laboratory.class);
        }
        return hm;
    }

    //Laboratory delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Laboratory> optLaboratory = labRepo.findById(id);
            if(optLaboratory.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticLaboratory el = elRepo.findById(id).get();
                labRepo.deleteById(id);
                elRepo.deleteById(el.getId());
                //File Delete
                String deleteFilePath = optLaboratory.get().getLabFileName();
                File file = new File(Util.UPLOAD_DIR + deleteFilePath);
                file.delete();
                //File Delete
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme i??lemi ba??ar??l??!");
                hm.put(ERest.result,optLaboratory.get());
            }else {
                String error = "Silmek istenen laboratuvar sonucu bulunamad??!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Laboratory.class);
            }
        } catch (Exception e) {
            String error = "Silme i??lemi s??ras??nda bir hata olu??tu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Laboratory.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - Start -------------------------------------//

    public Map<ERest,Object> elasticSearch(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla aram kriteri eklemek i??in multiMatchQuery yap??s?? kullan??l??r.
                .withQuery(multiMatchQuery(data,"cuname")
                        .field("paname")
                        .field("paAirTagNo")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticLaboratory>> list = operations.search(query,ElasticLaboratory.class).getSearchHits();
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
        List<Laboratory> laboratoryList = labRepo.findAll();
        try {
            if(laboratoryList.size() > 0){
                laboratoryList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticLaboratory el = new ElasticLaboratory();
                    el.setLabId(item.getLabId());
                    el.setCuname(item.getCustomer().getCuName());
                    el.setPaname(item.getPatient().getPaName());
                    el.setPaAirTagNo(item.getPatient().getPaAirTagNo());
                    elRepo.save(el);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme i??lemi ba??ar??l??!");
                hm.put(ERest.result,elRepo.findAll());
            }else {
                String error = "Sisteme kay??tl?? laboratuvar sounucu bulunmamaktad??r!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Laboratory.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri taban??na ekleme yap??l??rken bir hata olu??tu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Laboratory.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
