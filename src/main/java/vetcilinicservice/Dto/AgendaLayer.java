package vetcilinicservice.Dto;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vetcilinicservice.Entities.Agenda;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Repositories._jpa.AgendaRepository;
import vetcilinicservice.Repositories._jpa.UserRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

@Service
public class AgendaLayer {

    final AgendaRepository aRepo;
    final UserRepository uRepo;
    public AgendaLayer(AgendaRepository aRepo, UserRepository uRepo) {
        this.aRepo = aRepo;
        this.uRepo = uRepo;
    }

    //Agenda Add
    public Map<ERest,Object> add(Agenda agenda, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Agenda ag = aRepo.save(agenda);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Ajanda not ekleme işlemi başarılı!");
                hm.put(ERest.result,ag);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Ajanda not ekleme işlemi sırasında bir hata oluştu!";
                Util.logger(error, Agenda.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //Agenda List
    public Map<ERest,Object> list(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Agenda> agendaList = new ArrayList<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = new User();
            Optional<User> userOptional = uRepo.findByuEmailEqualsIgnoreCase(email);
            if (userOptional.isPresent()){
                user = userOptional.get();
                agendaList = aRepo.findByUid(user.getUId());
                hm.put(ERest.status,true);
                hm.put(ERest.message, "Ajanda not listeleme işlemi başarılı!");
                hm.put(ERest.result,agendaList);
            }else
            {
                hm.put(ERest.status,false);
                hm.put(ERest.message, "Kullanıcı bulunamadı.Lütfen giriş yapınız!");
            }

        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Agenda.class);
        }
        return hm;
    }

    //Agenda delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Agenda> optAgenda = aRepo.findById(id);
            if(optAgenda.isPresent()){
                aRepo.deleteById(id);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optAgenda.get());
            }else {
                String error = "Silmek istenen ajanda notu bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Agenda.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Agenda.class);
        }
        return hm;
    }

    //Agenda update
    public Map<ERest,Object> update(Agenda agenda,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(agenda.getAgendaId() != null && !bindingResult.hasErrors()){
            Optional<Agenda> optAgenda = aRepo.findById(agenda.getAgendaId());
            if(optAgenda.isPresent()){
                try {
                    Agenda ag = aRepo.saveAndFlush(agenda);
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,ag);
                } catch (Exception e) {
                    hm.put(ERest.status,false);
                    String error = "Ajanda not ekleme işlemi sırasında bir hata oluştu!";
                    Util.logger(error, Agenda.class);
                    hm.put(ERest.message,error);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak ajanda notu bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,agenda);
                Util.logger(error,Agenda.class);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.message,Util.errors(bindingResult));
        }
        return hm;
    }

}
