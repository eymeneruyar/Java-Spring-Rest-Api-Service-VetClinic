package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.SettingsUserLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/settingsUsers")
@Api(value = "SettingsUsersRestController",authorizations = {@Authorization(value = "basicAuth")})
public class SettingsUserRestController {

    final SettingsUserLayer suLayer;
    public SettingsUserRestController(SettingsUserLayer suLayer) {
        this.suLayer = suLayer;
    }

    //User add
    @PostMapping("/add")
    @ApiOperation(value = "Sisteme kullanıcı eklenmesi.")
    public Map<ERest,Object> add(@RequestBody @Valid User user, BindingResult bindingResult){
        return suLayer.add(user,bindingResult);
    }

    //User list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Kullanıcıların sayfa numarasına göre sıralanması.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return suLayer.list(stPage);
    }

    //User delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Kullanıcı silme.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return suLayer.delete(stId);
    }

    //User update
    @PutMapping("/update")
    @ApiOperation(value = "Kullanıcı güncelleme.")
    public Map<ERest,Object> update(@RequestBody @Valid User user,BindingResult bindingResult){
        return suLayer.update(user,bindingResult);
    }

    //User search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Kullanıcı arama.")
    public Map<ERest,Object> search(@PathVariable String data){
        return suLayer.elasticSearch(data);
    }

    //User insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Kullanıcı bilgilerinin elasticsearch veri tabanına eklenmesi.")
    public Map<ERest,Object> elasticInsertData(){
        return suLayer.elasticInsertData();
    }

}
