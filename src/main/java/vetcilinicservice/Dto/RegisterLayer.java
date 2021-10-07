package vetcilinicservice.Dto;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterLayer {

    final UserRepository uRepo;
    final RoleRepository roleRepo;
    final ElasticUserRepository euRepo;
    final UserService uService;
    public RegisterLayer(UserRepository uRepo, RoleRepository roleRepo, ElasticUserRepository euRepo, UserService uService) {
        this.uRepo = uRepo;
        this.roleRepo = roleRepo;
        this.euRepo = euRepo;
        this.uService = uService;
    }

    public Map<ERest,Object> add(User user, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();

        if (!bindingResult.hasErrors()) {

            try {
                int roleId = user.getURoleStatus();
                Role role = roleRepo.findById(roleId).get();
                List<Role> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                User us = uService.register(user);
                //Elasticsearch save
                ElasticUser eu = new ElasticUser();
                eu.setUId(us.getUId());
                eu.setName(us.getUName());
                eu.setSurname(us.getUSurname());
                eu.setEmail(us.getUEmail());
                eu.setPhone(us.getUPhone());
                euRepo.save(eu);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Kullanıcı başarılı bir şekilde kaydedildi.");
                hm.put(ERest.result,us);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                if(e.toString().contains("constraint")){
                    String error = "Bu e-mail ("+user.getUEmail()+") adresi ile daha önce kayıt yapılmış!";
                    Util.logger(error,User.class);
                    hm.put(ERest.message,error);
                }
            }

        } else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors,Util.errors(bindingResult));
        }
        return hm;
    }

}
