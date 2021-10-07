package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.VaccineDefinitionLayer;
import vetcilinicservice.Entities.Vaccine;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/vaccineDefinition")
@Api(value = "VaccineDefinitionRestController",authorizations = {@Authorization(value = "basicAuth")})
public class VaccineDefinitionRestController {

    final VaccineDefinitionLayer vacLayer;
    public VaccineDefinitionRestController(VaccineDefinitionLayer vacLayer) {
        this.vacLayer = vacLayer;
    }

    //Vaccine add
    @PostMapping("/add")
    @ApiOperation(value = "Aşı ekleme.")
    public Map<ERest,Object> add(@RequestBody @Valid Vaccine vaccine, BindingResult bindingResult){
        return vacLayer.add(vaccine,bindingResult);
    }

    //All Vaccine List
    @GetMapping("/listAll")
    @ApiOperation(value = "Aşı listeleme.")
    public Map<ERest,Object> listAll(){
        return vacLayer.listAll();
    }

    //Vaccine list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Sayfa numarasına göre aşı listeleme.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return vacLayer.list(stPage);
    }

    //Vaccine delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Aşı silme.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return vacLayer.delete(stId);
    }

    //Vaccine update
    @PutMapping("/update")
    @ApiOperation(value = "Aşı güncelleme.")
    public Map<ERest,Object> update(@RequestBody @Valid Vaccine vaccine,BindingResult bindingResult){
        return vacLayer.update(vaccine,bindingResult);
    }

    //Vaccine search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Aşı arama.")
    public Map<ERest,Object> search(@PathVariable String data){
        return vacLayer.elasticSearch(data);
    }

    //Vaccine insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Elasticsearch veri tabanına hazır aşı bilgilerini ekleme.")
    public Map<ERest,Object> elasticInsertData(){
        return vacLayer.elasticInsertData();
    }

}
