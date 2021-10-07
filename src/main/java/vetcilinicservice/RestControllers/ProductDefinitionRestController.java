package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.ProductDefinitionLayer;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Product;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/productDefinition")
@Api(value = "ProductDefinitionRestController",authorizations = {@Authorization(value = "basicAuth")})
public class ProductDefinitionRestController {

    final ProductDefinitionLayer pLayer;
    public ProductDefinitionRestController(ProductDefinitionLayer pLayer){
        this.pLayer = pLayer;
    }

    //Product add
    @PostMapping("/add")
    @ApiOperation(value = "Firmaya ait ürünlerin eklenmesi.")
    public Map<ERest,Object> add(@RequestBody @Valid Product product, BindingResult bindingResult){
        return pLayer.add(product,bindingResult);
    }

    //All Product List
    @GetMapping("/listAll")
    @ApiOperation(value = "Firmaya ait ürünlerin sıralanması.")
    public Map<ERest,Object> listAll(){
        return pLayer.listAll();
    }

    //Product list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Firmaya ait ürünlerin sayfa numarasına göre sıralanması.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return pLayer.list(stPage);
    }

    //Product delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Firmaya ait ürünlerin silinmesi.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return pLayer.delete(stId);
    }

    //Product update
    @PutMapping("/update")
    @ApiOperation(value = "Firmaya ait ürünlerin güncellenmesi.")
    public Map<ERest,Object> update(@RequestBody @Valid Product product,BindingResult bindingResult){
        return pLayer.update(product,bindingResult);
    }

    //Product search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Firmaya ait ürünlerin aranması.")
    public Map<ERest,Object> search(@PathVariable String data){
        return pLayer.elasticSearch(data);
    }

    //Product insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Firmaya ait ürünlerin elasticsearch veri tabanına eklenmesi.")
    public Map<ERest,Object> elasticInsertData(){
        return pLayer.elasticInsertData();
    }

}
