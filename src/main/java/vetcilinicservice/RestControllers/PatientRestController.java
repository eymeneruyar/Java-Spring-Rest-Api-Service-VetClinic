package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.PatientLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Patient;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@Api(value = "PatientRestController",authorizations = {@Authorization(value = "basicAuth")})
public class PatientRestController {

    final PatientLayer paLayer;
    public PatientRestController(PatientLayer paLayer) {
        this.paLayer = paLayer;
    }

    //Patient add
    @PostMapping("/add")
    @ApiOperation(value = "Hasta ekleme işlemini yapar.")
    public Map<ERest,Object> add(@RequestBody @Valid Patient patient, BindingResult bindingResult){
        return paLayer.add(patient,bindingResult);
    }

    //Patient List with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Sayfa numarasına göre hastaları listeler.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return paLayer.list(stPage);
    }

    //Patient list with customer ID
    @GetMapping("/listWithCuId/{stId}")
    @ApiOperation(value = "Müşteriye ait hastaları getirmektedir.")
    public Map<ERest,Object> listWithCuId(@PathVariable String stId){
        return paLayer.listWithCuId(stId);
    }

    //Patient delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Hasta bilgilerini siler.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return paLayer.delete(stId);
    }

    //Patient update
    @PutMapping("/update")
    @ApiOperation(value = "Hasta bilgilerini günceller.")
    public Map<ERest,Object> update(@RequestBody @Valid Patient patient, BindingResult bindingResult){
        return paLayer.update(patient,bindingResult);
    }

    //Patient search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Hasta araması yapar.")
    public Map<ERest,Object> search(@PathVariable String data){
        return paLayer.elasticSearch(data);
    }

    //Patient insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Mevcut hasta verilerini elasticsearch veri tabanına ekler.")
    public Map<ERest,Object> elasticInsertData(){
        return paLayer.elasticInsertData();
    }

}
