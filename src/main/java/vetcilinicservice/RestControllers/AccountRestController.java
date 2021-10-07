package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vetcilinicservice.Dto.AccountLayer;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Utils.ERest;

import java.util.Map;

@RestController
@RequestMapping("/account")
@Api(value = "AccountRestController",authorizations = {@Authorization(value = "basicAuth")})
public class AccountRestController {

    final AccountLayer acLayer;
    public AccountRestController(AccountLayer acLayer) {
        this.acLayer = acLayer;
    }

    //Upload profile image
    @PostMapping("/upload")
    @ApiOperation(value = "Profil resmi yükler.")
    public Map<ERest, Object> upload(@RequestPart("fileName") MultipartFile file){
        return acLayer.upload(file);
    }

    //Change password in active user
    @PostMapping("/changePass")
    @ApiOperation(value = "Hesap şifresini değiştirir.")
    public Map<ERest,Object> changePassword(@RequestParam String newPass,@RequestParam String reNewPass){
        return acLayer.changePassword(newPass,reNewPass);
    }

}
