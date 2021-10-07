package vetcilinicservice.RestControllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vetcilinicservice.Services.UserService;
import vetcilinicservice.Utils.ERest;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class AccountActivityRestController {

    final UserService uService;
    public AccountActivityRestController(UserService uService) {
        this.uService = uService;
    }

    @GetMapping("/accountActivity")
    @ApiOperation(value = "Sistemde olan kullanıcının bilgilerini getirir.")
    public Map<ERest,Object> list(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        hm.put(ERest.status,true);
        hm.put(ERest.result,uService.info());
        return hm;
    }

}
