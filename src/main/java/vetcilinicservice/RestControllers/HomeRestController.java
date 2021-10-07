package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vetcilinicservice.Dto.HomeLayer;
import vetcilinicservice.Utils.ERest;

import java.util.Map;

@RestController
@RequestMapping("/home")
@Api(value = "HomeRestController",authorizations = {@Authorization(value = "basicAuth")})
public class HomeRestController {

    final HomeLayer homeLayer;
    public HomeRestController(HomeLayer homeLayer) {
        this.homeLayer = homeLayer;
    }

    //Kazançlar Grafiği
    @GetMapping("/priceChart")
    @ApiOperation(value = "Kazanç istatistiklerini getirmektedir.")
    public Map<ERest,Object> priceChart(){
        return homeLayer.priceChart();
    }

    //Genel İstatistikler Bilgisi
    @GetMapping("/generalStatics")
    @ApiOperation(value = "Genel istatistik bilgilerini verir.")
    public Map<ERest,Object> generalStatics(){
        return homeLayer.generalStatics();
    }

    //Günlük Giriş Yapan Hasta Bilgileri Tablosu
    @GetMapping("/patientTable")
    @ApiOperation(value = "Günlük giriş yapan hasta bilgilerini getirir.")
    public  Map<ERest,Object> patientList(){
        return homeLayer.patientList();
    }

    //Günlük Randevu Bilgilendirme Kartı
    @GetMapping("/schedule/{stPageNo}")
    @ApiOperation(value = "Günlük randevu bilgilerini getirir.")
    public Map<ERest,Object> scheduleAppointment(@PathVariable String stPageNo){
        return homeLayer.scheduleAppointment(stPageNo);
    }

}
