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
                    System.err.println("Dosya boyutu çok büyük Max 2MB");
                    errorMessage = "Dosya boyutu çok büyük Max "+ (maxFileUploadSize / 1024) +"MB olmalıdır";
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
                    hm.put(ERest.message,"Laboratuvar sonucu ekleme işlemi başarılı!");
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
                    String error = "Laboratuvar sonucu eklerken bir hata oluştu!";
                    Util.logger(error, Laboratory.class);
                    hm.put(ERest.message,error);
                }
            }else{
                String error = "Lütfen bir dosya seçiniz!";
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
            hm.put(ERest.message,(page) + " sayfadaki laboratuvar sonucu listeleme işlemi başarılı!");
            hm.put(ERest.result,laboratoryList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
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
                hm.put(ERest.message,"Laboratuvar sonucu detayı başarılı bir şekilde getirildi.");
                hm.put(ERest.result,optDetail.get());
            }else{
                String error = "Laboratuvar sonucu bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Laboratory.class);
            }
        } catch (Exception e) {
            String error = "Laboratuvar sonuç detayı getirilirken bir hata oluştu!";
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
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optLaboratory.get());
            }else {
                String error = "Silmek istenen laboratuvar sonucu bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Laboratory.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
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
                //Birden fazla aram kriteri eklemek için multiMatchQuery yapısı kullanılır.
                .withQuery(multiMatchQuery(data,"cuname")
                        .field("paname")
                        .field("paAirTagNo")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticLaboratory>> list = operations.search(query,ElasticLaboratory.class).getSearchHits();
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
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,elRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı laboratuvar sounucu bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Laboratory.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Laboratory.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
