package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.StorageLayer;
import vetcilinicservice.Entities.Storage;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/storage")
@Api(value = "StorageRestController",authorizations = {@Authorization(value = "basicAuth")})
public class StorageRestController {

    final StorageLayer sLayer;
    public StorageRestController(StorageLayer sLayer){
        this.sLayer = sLayer;
    }

    //Storage add
    @PostMapping("/add")
    @ApiOperation(value = "Depo ekleme.")
    public Map<ERest,Object> add(@RequestBody @Valid Storage storage, BindingResult bindingResult){
        return sLayer.add(storage,bindingResult);
    }

    //All Storage List
    @GetMapping("/listAll")
    @ApiOperation(value = "Depo listeleme.")
    public Map<ERest,Object> listAll(){
        return sLayer.listAll();
    }

    //Storage list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Sayfa numarasına göre depo listeleme.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return sLayer.list(stPage);
    }

    //Storage delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Depo silme.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return sLayer.delete(stId);
    }

    //Storage update
    @PutMapping("/update")
    @ApiOperation(value = "Depo güncelleme.")
    public Map<ERest,Object> update(@RequestBody @Valid Storage storage,BindingResult bindingResult){
        return sLayer.update(storage,bindingResult);
    }

    //Storage search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Depo arama.")
    public Map<ERest,Object> search(@PathVariable String data){
        return sLayer.elasticSearch(data);
    }

    //Storage insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Depo bilgilerini elasticsearch veri tabanına ekleme.")
    public Map<ERest,Object> elasticInsertData(){
        return sLayer.elasticInsertData();
    }

}
