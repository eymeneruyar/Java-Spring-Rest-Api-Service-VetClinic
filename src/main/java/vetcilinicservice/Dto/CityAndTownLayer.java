package vetcilinicservice.Dto;

import org.springframework.stereotype.Service;
import vetcilinicservice.Entities.Cities;
import vetcilinicservice.Entities.Towns;
import vetcilinicservice.Model.RedisCities;
import vetcilinicservice.Model.RedisTowns;
import vetcilinicservice.Repositories._jpa.CitiesRepository;
import vetcilinicservice.Repositories._jpa.TownsRepository;
import vetcilinicservice.Repositories._redis.RedisCitiesRepository;
import vetcilinicservice.Repositories._redis.RedisTownsRepository;
import vetcilinicservice.Utils.ERest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CityAndTownLayer {

    final RedisCitiesRepository rcRepo;
    final RedisTownsRepository rtRepo;
    final CitiesRepository cRepo;
    final TownsRepository tRepo;
    public CityAndTownLayer(RedisCitiesRepository rcRepo, RedisTownsRepository rtRepo, CitiesRepository cRepo, TownsRepository tRepo) {
        this.rcRepo = rcRepo;
        this.rtRepo = rtRepo;
        this.cRepo = cRepo;
        this.tRepo = tRepo;
    }

    //MySQL veri tabanımdan bulunan il ve ilçe bilgilerini Redis veri tabanına yazma işlemi.
    public Map<ERest,Object> add(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Cities> cityList = cRepo.findAll();
        List<Towns> townList = tRepo.findAll();


        if(cityList != null && townList != null){
            if(cityList.size() > 0 && townList.size() > 0){
                hm.put(ERest.status,true);
                hm.put(ERest.message,"İl ve İlçe bilgileri Redis veri tabanına başarılı bir şekilde aktarıldı!");
                cityList.forEach(item -> {
                    RedisCities rc = new RedisCities();
                    rc.setId(item.getId());
                    rc.setCityKey(item.getCityKey());
                    rc.setName(item.getName());
                    rcRepo.save(rc);
                });
                townList.forEach(item -> {
                    RedisTowns rt = new RedisTowns();
                    rt.setId(item.getId());
                    rt.setName(item.getName());
                    rt.setTownCityKey(item.getTownCityKey());
                    rt.setTownKey(item.getTownKey());
                    rtRepo.save(rt);
                });
            }else{
                hm.put(ERest.status,true);
                hm.put(ERest.message,"İl ve İlçe bilgisi bulunmamaktadır!");
            }
        }else{
            hm.put(ERest.status,false);
            hm.put(ERest.message,"Redis veri tabanına veriler yüklenirken bir hata oluştu!");
            hm.put(ERest.result,null);
        }
        return hm;
    }

    //Redis veri tabanından bütün şehirleri sıralama
    public Map<ERest,Object> listCity(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        hm.put(ERest.status,true);
        hm.put(ERest.message,"Şehir listeleme işlemi başarılı!");
        hm.put(ERest.result,rcRepo.findAll());
        return hm;
    }

    //Plaka koduna göre ilçelerin listelenmesi.
    public Map<ERest,Object> listTowns(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Şehir koduna göre ilçe listeleme işlemi başarılı!");
            hm.put(ERest.result,rtRepo.findByTownCityKeyEquals(id));
        } catch (Exception e) {
            hm.put(ERest.status,false);
            hm.put(ERest.message,"Listeleme işlemi sırasında bir hata oluştu!");
        }
        return hm;
    }

}
