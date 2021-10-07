package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.BuyingLayer;
import vetcilinicservice.Entities.Buying;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/buying")
@Api(value = "BuyingRestController",authorizations = {@Authorization(value = "basicAuth")})
public class BuyingRestController {

    final BuyingLayer buyLayer;
    public BuyingRestController(BuyingLayer buyLayer) {
        this.buyLayer = buyLayer;
    }

    //Buying add
    @PostMapping("/add")
    @ApiOperation(value = "Firmaya ürün alış işlemi yapar.")
    public Map<ERest,Object> add(@RequestBody @Valid Buying buying, BindingResult bindingResult){
        return buyLayer.add(buying,bindingResult);
    }

    //Buying list with pagination
    @GetMapping("/list/{stPage}")
    @ApiOperation(value = "Yapılan alım işlemlerini sayfa numarasına göre sıralar.")
    public Map<ERest,Object> list(@PathVariable String stPage){
        return buyLayer.list(stPage);
    }

    //Product list with supplier ID
    @GetMapping("/listWithSupId/{stId}")
    @ApiOperation(value = "Seçilen tedarikçinin ürünleri getirilir.")
    public Map<ERest,Object> listWithSupId(@PathVariable String stId){
        return buyLayer.listWithSupId(stId);
    }

    //Buying search with elasticsearch
    @GetMapping("/search/{data}")
    @ApiOperation(value = "Arama işlemi yapılır.")
    public Map<ERest,Object> search(@PathVariable String data){
        return buyLayer.elasticSearch(data);
    }

    //Buying insert all data to elasticsearch database
    @GetMapping("/elasticInsertData")
    @ApiOperation(value = "Hazır veriler elasticsearch veri tabanına eklenir.")
    public Map<ERest,Object> elasticInsertData(){
        return buyLayer.elasticInsertData();
    }

}
