package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.CustomerLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@Api(value = "CustomerRestController",authorizations = {@Authorization(value = "basicAuth")})
public class CustomerRestController {

    final CustomerLayer cuLayer;
    public CustomerRestController(CustomerLayer cuLayer) {
        this.cuLayer = cuLayer;
    }

    //Customer add
    @PostMapping("/add")
    @ApiOperation(value = "Müşteri ekler.")
    public Map<ERest,Object> add(@RequestBody @Valid Customer customer, BindingResult bindingResult){
        return cuLayer.add(customer,bindingResult);
    }

    //All Customer List
    @GetMapping("/listAll")
    @ApiOperation(value = "Bütün müşterileri sıralar.")
    public Map<ERest,Object> listAll(){
        return cuLayer.listAll();
    }

    //Customer list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Verilen sayfa numarasına göre sıralar.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return cuLayer.list(stPage);
    }

    //Customer delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Müşteriyi siler.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return cuLayer.delete(stId);
    }

    //Customer update
    @PutMapping("/update")
    @ApiOperation(value = "Müşteri bilgilerini günceller.")
    public Map<ERest,Object> update(@RequestBody @Valid Customer customer,BindingResult bindingResult){
        return cuLayer.update(customer,bindingResult);
    }

    //Customer search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Arama işlemi yapar.")
    public Map<ERest,Object> search(@PathVariable String data){
        return cuLayer.elasticSearch(data);
    }

    //Customer insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Elasticsearch veri tabanına hazır verileri aktarma.")
    public Map<ERest,Object> elasticInsertData(){
        return cuLayer.elasticInsertData();
    }

}
