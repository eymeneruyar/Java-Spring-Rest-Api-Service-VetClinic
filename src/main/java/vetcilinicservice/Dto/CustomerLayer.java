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
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Supplier;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Repositories._jpa.CustomerRepository;
import vetcilinicservice.Repositories._elastic.ElasticCustomerRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

import java.util.*;

@Service
public class CustomerLayer {

    final CustomerRepository cuRepo;
    final ElasticCustomerRepository ecuRepo;
    final ElasticsearchOperations operations;
    public CustomerLayer(CustomerRepository cuRepo, ElasticCustomerRepository ecuRepo, ElasticsearchOperations operations) {
        this.cuRepo = cuRepo;
        this.ecuRepo = ecuRepo;
        this.operations = operations;
    }

    //Customer Add
    public Map<ERest,Object> add(Customer customer, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Customer cu = cuRepo.save(customer);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Müşteri ekleme işlemi başarılı!");
                hm.put(ERest.result,cu);
                //Elasticsearch save
                ElasticCustomer ec = new ElasticCustomer();
                ec.setCuid(cu.getCuId());
                ec.setEmail(cu.getCuEmail());
                ec.setName(cu.getCuName());
                ec.setSurname(cu.getCuSurname());
                ec.setPhone(cu.getCuPhone());
                ecuRepo.save(ec);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                if(e.toString().contains("constraint")){
                    String error = "Bu e-mail ("+customer.getCuEmail()+") adresi ile daha önce kayıt yapılmış!";
                    Util.logger(error, Customer.class);
                    hm.put(ERest.message,error);
                }
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All Customer List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Customer> customerList = new ArrayList<>();
        try {
            customerList = cuRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Müşteri listeleme işlemi başarılı!");
            hm.put(ERest.result,customerList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Customer.class);
        }
        return hm;
    }

    //Customer List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Customer> customerList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            customerList = cuRepo.findByOrderByCuIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki müşteri listeleme işlemi başarılı!");
            hm.put(ERest.result,customerList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Customer.class);
        }
        return hm;
    }

    //Customer delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Customer> optCustomer = cuRepo.findById(id);
            if(optCustomer.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticCustomer ec = ecuRepo.findById(id).get();
                cuRepo.deleteById(id);
                ecuRepo.deleteById(ec.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optCustomer.get());
            }else {
                String error = "Silmek istenen müşteri bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Customer.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Customer.class);
        }
        return hm;
    }

    //Customer update
    public Map<ERest,Object> update(Customer customer,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(customer.getCuId() != null && !bindingResult.hasErrors()){
            Optional<Customer> optCustomer = cuRepo.findById(customer.getCuId());
            if(optCustomer.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticCustomer ec = ecuRepo.findById(customer.getCuId()).get();
                    ecuRepo.deleteById(ec.getId());
                    Customer cu = cuRepo.saveAndFlush(customer);
                    ElasticCustomer ecNew = new ElasticCustomer();
                    ecNew.setCuid(cu.getCuId());
                    ecNew.setEmail(cu.getCuEmail());
                    ecNew.setName(cu.getCuName());
                    ecNew.setSurname(cu.getCuSurname());
                    ecNew.setPhone(cu.getCuPhone());
                    ecuRepo.save(ecNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,cu);
                } catch (Exception e) {
                    String error = "Güncelleme işlemi sırasında bir hata oluştu! " + e + " ";
                    hm.put(ERest.status,false);
                    if(e.toString().contains("constraint")){
                        error += "Bu e-mail ("+customer.getCuEmail()+") adresi ile daha önce kayıt yapılmış";
                        hm.put(ERest.message,error);
                    }
                    hm.put(ERest.result,customer);
                    Util.logger(error,Customer.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak müşteri bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,customer);
                Util.logger(error,Customer.class);
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
                        .field("surname")
                        .field("phone")
                        .field("email")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticCustomer>> list = operations.search(query,ElasticCustomer.class).getSearchHits();
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
        List<Customer> customerList = cuRepo.findAll();
        try {
            if(customerList.size() > 0){
                customerList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticCustomer ec = new ElasticCustomer();
                    ec.setCuid(item.getCuId());
                    ec.setEmail(item.getCuEmail());
                    ec.setName(item.getCuName());
                    ec.setSurname(item.getCuSurname());
                    ec.setPhone(item.getCuPhone());
                    ecuRepo.save(ec);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,ecuRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı müşteri bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Customer.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Customer.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
