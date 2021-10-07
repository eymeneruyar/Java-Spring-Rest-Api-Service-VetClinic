package vetcilinicservice.Dto;

import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vetcilinicservice.Documents.ElasticUser;
import vetcilinicservice.Entities.Role;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Repositories._elastic.ElasticUserRepository;
import vetcilinicservice.Repositories._jpa.RoleRepository;
import vetcilinicservice.Repositories._jpa.UserRepository;
import vetcilinicservice.Services.UserService;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class SettingsUserLayer {

    final UserRepository uRepo;
    final RoleRepository roleRepo;
    final UserService uService;
    final ElasticUserRepository euRepo;
    final ElasticsearchOperations operations;
    public SettingsUserLayer(UserRepository uRepo, RoleRepository roleRepo, UserService uService, ElasticUserRepository euRepo, ElasticsearchOperations operations) {
        this.uRepo = uRepo;
        this.roleRepo = roleRepo;
        this.uService = uService;
        this.euRepo = euRepo;
        this.operations = operations;
    }

    //User Add
    public Map<ERest,Object> add(User user, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                int roleId = user.getURoleStatus();
                Role role = roleRepo.findById(roleId).get();
                List<Role> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                User us = uService.register(user);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Kullanıcı ekleme işlemi başarılı!");
                hm.put(ERest.result,us);
                //Elasticsearch save
                ElasticUser eu = new ElasticUser();
                eu.setUId(us.getUId());
                eu.setName(us.getUName());
                eu.setSurname(us.getUSurname());
                eu.setEmail(us.getUEmail());
                eu.setPhone(us.getUPhone());
                euRepo.save(eu);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                if(e.toString().contains("constraint")){
                    String error = "Bu e-mail ("+user.getUEmail()+") adresi ile daha önce kayıt yapılmış!";
                    Util.logger(error, User.class);
                    hm.put(ERest.message,error);
                }
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //User List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<User> userList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            Page<User> pageList = uRepo.findAllUsersWithPagination(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki kullanıcı listeleme işlemi başarılı!");
            hm.put(ERest.result,pageList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,User.class);
        }
        return hm;
    }

    //User delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<User> optUser = uRepo.findById(id);
            if(optUser.isPresent()){
                System.out.println("id " + id + " " + uRepo.findById(id).get());
                //Elasticsearch database and MySQL database delete data.
                ElasticUser eu = euRepo.findById(id).get();
                uRepo.deleteById(id);
                euRepo.deleteById(eu.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optUser.get());
            }else {
                String error = "Silmek istenen kullanıcı bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,User.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,User.class);
        }
        return hm;
    }

    //User update
    public Map<ERest,Object> update(User user,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(user.getUId() != null && !bindingResult.hasErrors()){
            Optional<User> optUser = uRepo.findById(user.getUId());
            if(optUser.isPresent()){
                try {
                    int roleId = user.getURoleStatus();
                    Role role = roleRepo.findById(roleId).get();
                    List<Role> roles = new ArrayList<>();
                    roles.add(role);
                    user.setRoles(roles);
                    user.setUPassword(optUser.get().getUPassword());
                    //ElasticSearch and SQL DB Update -Start
                    ElasticUser eu = euRepo.findById(user.getUId()).get();
                    euRepo.deleteById(eu.getId());
                    User us = uRepo.saveAndFlush(user);
                    ElasticUser euNew = new ElasticUser();
                    euNew.setUId(us.getUId());
                    euNew.setName(us.getUName());
                    euNew.setSurname(us.getUSurname());
                    euNew.setEmail(us.getUEmail());
                    euNew.setPhone(us.getUPhone());
                    euRepo.save(euNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,us);
                } catch (Exception e) {
                    String error = "Güncelleme işlemi sırasında bir hata oluştu! " + e + " ";
                    hm.put(ERest.status,false);
                    if(e.toString().contains("constraint")){
                        error += "Bu e-mail ("+user.getUEmail()+") adresi ile daha önce kayıt yapılmış";
                        hm.put(ERest.message,error);
                    }
                    hm.put(ERest.result,user);
                    Util.logger(error,User.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak kullanıcı bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,user);
                Util.logger(error,User.class);
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
                        .field("email")
                        .field("phone")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticUser>> list = operations.search(query,ElasticUser.class).getSearchHits();
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
        List<User> userList = uRepo.findAll();
        try {
            if(userList.size() > 0){
                userList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticUser eu = new ElasticUser();
                    eu.setUId(item.getUId());
                    eu.setName(item.getUName());
                    eu.setSurname(item.getUSurname());
                    eu.setEmail(item.getUEmail());
                    eu.setPhone(item.getUPhone());
                    euRepo.save(eu);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,euRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı kullanıcı bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,User.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,User.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
