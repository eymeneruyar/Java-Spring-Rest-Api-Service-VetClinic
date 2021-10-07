package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vetcilinicservice.Dto.LaboratoryLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Laboratory;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/laboratory")
@Api(value = "LaboratoryRestController",authorizations = {@Authorization(value = "basicAuth")})
public class LaboratoryRestController {

    final LaboratoryLayer labLayer;
    public LaboratoryRestController(LaboratoryLayer labLayer) {
        this.labLayer = labLayer;
    }

    //Laboratory add
    @PostMapping("/add")
    @ApiOperation(value = "Laboratuvar sonucu ekleme.")
    public Map<ERest,Object> add(@RequestPart("fileName") MultipartFile file, @Valid Laboratory laboratory, BindingResult bindingResult){
        return labLayer.add(file,laboratory,bindingResult);
    }

    //Laboratory list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Laboratuvar sonuçlarının sayfa numarasına göre sıralanması.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return labLayer.list(stPage);
    }

    //Laboratory result detail
    @GetMapping("/detail/{stId}")
    @ApiOperation(value = "Laboratuvar sonucuna ait detayları getirir.")
    public Map<ERest,Object> detail(@PathVariable String stId){
        return labLayer.detail(stId);
    }

    //Laboratory delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Laboratuvar sonucunu siler.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return labLayer.delete(stId);
    }

    //Laboratory search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "LAboratuvar sonuçları için arama yapar.")
    public Map<ERest,Object> search(@PathVariable String data){
        return labLayer.elasticSearch(data);
    }

    //Laboratory insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Laboratuvar sonuçlarını elasticsearch veri tabanına ekler.")
    public Map<ERest,Object> elasticInsertData(){
        return labLayer.elasticInsertData();
    }

}
