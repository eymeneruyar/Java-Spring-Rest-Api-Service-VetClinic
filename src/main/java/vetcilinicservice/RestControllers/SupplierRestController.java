package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.SupplierLayer;
import vetcilinicservice.Entities.Supplier;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/supplier")
@Api(value = "SupplierRestController",authorizations = {@Authorization(value = "basicAuth")})
public class SupplierRestController {

    final SupplierLayer supLayer;
    public SupplierRestController(SupplierLayer supLayer) {
        this.supLayer = supLayer;
    }

    //Supplier add
    @PostMapping("/add")
    @ApiOperation(value = "Tedarikçi ekleme.")
    public Map<ERest,Object> add(@RequestBody @Valid Supplier supplier, BindingResult bindingResult){
        return supLayer.add(supplier,bindingResult);
    }

    //All Supplier List
    @GetMapping("/listAll")
    @ApiOperation(value = "Tedarikçi listeleme.")
    public Map<ERest,Object> listAll(){
        return supLayer.listAll();
    }

    //Supplier list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Sayfa numarasına göre tedarikçi listeleme.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return supLayer.list(stPage);
    }

    //Supplier delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Tedarikçi silme.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return supLayer.delete(stId);
    }

    //Supplier update
    @PutMapping("/update")
    @ApiOperation(value = "Tedarikçi düzenleme.")
    public Map<ERest,Object> update(@RequestBody @Valid Supplier supplier, BindingResult bindingResult){
        return supLayer.update(supplier,bindingResult);
    }

    //Supplier search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Tedarikçi arama.")
    public Map<ERest,Object> search(@PathVariable String data){
        return supLayer.elasticSearch(data);
    }

    //Supplier insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Elasticsearch veri tabanına tedarikçi ekleme.")
    public Map<ERest,Object> elasticInsertData(){
        return supLayer.elasticInsertData();
    }

}
