package vetcilinicservice.RestControllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vetcilinicservice.Dto.RegisterLayer;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterRestController {

    final RegisterLayer registerLayer;
    public RegisterRestController(RegisterLayer registerLayer) {
        this.registerLayer = registerLayer;
    }

    @PostMapping("/add")
    @ApiOperation(value = "Yeni Kullanıcı Kayıt Servisi")
    public Map<ERest,Object> add(@RequestBody @Valid User user, BindingResult bindingResult){
        return registerLayer.add(user,bindingResult);
    }

}
