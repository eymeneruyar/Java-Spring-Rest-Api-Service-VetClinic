package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.SalesLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Sales;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/sales")
@Api(value = "SalesRestController",authorizations = {@Authorization(value = "basicAuth")})
public class SalesRestController {

    final SalesLayer sLayer;
    public SalesRestController(SalesLayer sLayer) {
        this.sLayer = sLayer;
    }

    //Sales add
    @PostMapping("/add")
    @ApiOperation(value = "Satış işleminin eklenmesi.")
    public Map<ERest,Object> add(@RequestBody @Valid Sales sales, BindingResult bindingResult){
        return sLayer.add(sales,bindingResult);
    }

    //Sales list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Satışların sayfa numarasına göre sıralanması.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return sLayer.list(stPage);
    }

    //Sales search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Yapılan satışların aranması.")
    public Map<ERest,Object> search(@PathVariable String data){
        return sLayer.elasticSearch(data);
    }

    //Sales insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Satışların elasticsearch veri tabanına eklenmesi.")
    public Map<ERest,Object> elasticInsertData(){
        return sLayer.elasticInsertData();
    }

}
