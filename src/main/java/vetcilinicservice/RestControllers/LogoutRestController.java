package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vetcilinicservice.Utils.ERest;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Api(value = "LogoutRestController",authorizations = {@Authorization(value = "basicAuth")})
public class LogoutRestController {

    @GetMapping("/logout")
    @ApiOperation(value = "Çıkış Yap Servisi")
    public Map<ERest,Object> logout(){
        Map<ERest,Object> hm = new LinkedHashMap<>();

        hm.put(ERest.status,true);
        hm.put(ERest.message,"Başarıyla çıkış yapıldı!");

        return hm;
    }

}
