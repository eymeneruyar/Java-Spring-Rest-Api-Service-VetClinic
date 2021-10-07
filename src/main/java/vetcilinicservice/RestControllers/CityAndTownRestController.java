package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vetcilinicservice.Dto.CityAndTownLayer;
import vetcilinicservice.Utils.ERest;

import java.util.Map;

@RestController
@RequestMapping("/cat")
@Api(value = "CityAndTownRestController",authorizations = {@Authorization(value = "basicAuth")})
public class CityAndTownRestController {

    final CityAndTownLayer catLayer;
    public CityAndTownRestController(CityAndTownLayer catLayer) {
        this.catLayer = catLayer;
    }

    //MySQL veri tabanımdan bulunan il ve ilçe bilgilerini Redis veri tabanına yazma işlemi.
    @GetMapping("/add")
    @ApiOperation(value = "Redis veri tabanına ekleme işlemi yapar.")
    public Map<ERest,Object> add(){
        return catLayer.add();
    }

    //Redis veri tabanından bütün şehirleri sıralama
    @GetMapping("/listCity")
    @ApiOperation(value = "Şehirleri listeler.")
    public Map<ERest,Object> listCity(){
        return catLayer.listCity();
    }

    //Plaka koduna göre ilçelerin listelenmesi.
    @GetMapping("/listTownsById/{stId}")
    @ApiOperation(value = "Şehir plaka koduna göre ilçeleri sıralar.")
    public Map<ERest,Object> listTowns(@PathVariable String stId){
        return catLayer.listTowns(stId);
    }

}
