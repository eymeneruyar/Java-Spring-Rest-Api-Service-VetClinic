package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.TreatmentLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Treatment;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/treatment")
@Api(value = "TreatmentRestController",authorizations = {@Authorization(value = "basicAuth")})
public class TreatmentRestController {

    final TreatmentLayer treLayer;
    public TreatmentRestController(TreatmentLayer treLayer) {
        this.treLayer = treLayer;
    }

    //Treatment add
    @PostMapping("/add")
    @ApiOperation(value = "Muayene ekleme.")
    public Map<ERest,Object> add(@RequestBody @Valid Treatment treatment, BindingResult bindingResult){
        return treLayer.add(treatment,bindingResult);
    }

    //Treatment list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Sayfa numarasına göre muayene listeleme.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return treLayer.list(stPage);
    }

    //Treatment delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Muayene silme.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return treLayer.delete(stId);
    }

    //Treatment update
    @PutMapping("/update")
    @ApiOperation(value = "Muayene güncelleme.")
    public Map<ERest,Object> update(@RequestBody @Valid Treatment treatment,BindingResult bindingResult){
        return treLayer.update(treatment,bindingResult);
    }

    //Treatment search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Muayene arama.")
    public Map<ERest,Object> search(@PathVariable String data){
        return treLayer.elasticSearch(data);
    }

    //Treatment insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Elasticsearch veri tabanına hazır muayene bilgilerini ekleme.")
    public Map<ERest,Object> elasticInsertData(){
        return treLayer.elasticInsertData();
    }

}
