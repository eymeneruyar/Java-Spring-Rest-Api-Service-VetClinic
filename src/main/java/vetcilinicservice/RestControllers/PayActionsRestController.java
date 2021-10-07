package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.PayInLayer;
import vetcilinicservice.Dto.PayOutLayer;
import vetcilinicservice.Entities.PayIn;
import vetcilinicservice.Entities.PayOut;
import vetcilinicservice.Utils.ERest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/payActions")
@Api(value = "PayActionsRestController",authorizations = {@Authorization(value = "basicAuth")})
public class PayActionsRestController {

    final PayInLayer payInLayer;
    final PayOutLayer payOutLayer;
    public PayActionsRestController(PayInLayer payInLayer,PayOutLayer payOutLayer) {
        this.payInLayer = payInLayer;
        this.payOutLayer = payOutLayer;
    }

    //------------------------------------------ PayIn Section - Start ------------------------------------------//

    //PayIn add
    @PostMapping("/payIn/add")
    @ApiOperation(value = "Kasa girişi eklenir. ")
    public Map<ERest,Object> addPayIn(@RequestBody @Valid PayIn payIn, BindingResult bindingResult){
        return payInLayer.addPayIn(payIn,bindingResult);
    }

    //All PayIn List
    @GetMapping("/payIn/listAll")
    @ApiOperation(value = "Kasa girişleri sıralanır.")
    public Map<ERest,Object> listAllPayIn(){
        return payInLayer.listAll();
    }

    //PayIn Invoice List with customer ID
    @GetMapping("/payIn/invoiceList/{stId}")
    @ApiOperation(value = "Müşteriye ait faturalar getirilir.")
    public Map<ERest,Object> invoiceListPayIn(@PathVariable String stId){
        return payInLayer.invoiceList(stId);
    }

    //PayIn delete
    @DeleteMapping("/payIn/delete/{stId}")
    @ApiOperation(value = "Kasa girişi silme işlemi.")
    public Map<ERest,Object> deletePayIn(@PathVariable String stId){
        return payInLayer.delete(stId);
    }

    //PayIn update
    @PutMapping("/payIn/update")
    @ApiOperation(value = "Kasa girişi güncelleme işlemi.")
    public Map<ERest,Object> updatePayIn(@RequestBody @Valid PayIn payIn,BindingResult bindingResult){
        return payInLayer.update(payIn,bindingResult);
    }

    //PayIn search with elasticsearch
    @GetMapping("/payIn/search/{data}")
    @ApiOperation(value = "Kasa girişi arama işlemi.")
    public Map<ERest,Object> searchPayIn(@PathVariable String data){
        return payInLayer.elasticSearchPayIn(data);
    }

    //PayIn insert all data to elasticsearch database
    @GetMapping("/payIn/elasticInsertData")
    @ApiOperation(value = "Kasa girişi hazır bilgileri elasticsearch veri tabanına ekleme.")
    public Map<ERest,Object> elasticInsertDataPayIn(){
        return payInLayer.elasticInsertDataPayIn();
    }

    //------------------------------------------ PayIn Section - End ------------------------------------------//

    //------------------------------------------ PayOut Section - Start ------------------------------------------//

    //PayOut add
    @PostMapping("/payOut/add")
    @ApiOperation(value = "Kasa çıkışı eklenir. ")
    public Map<ERest,Object> addPayOut(@RequestBody @Valid PayOut payOut, BindingResult bindingResult){
        return payOutLayer.addPayOut(payOut,bindingResult);
    }

    //PayOut PayIn List
    @GetMapping("/payOut/listAll")
    @ApiOperation(value = "Kasa çıkışları sıralanır.")
    public Map<ERest,Object> listAllPayOut(){
        return payOutLayer.listAll();
    }

    //PayOut Invoice List with Supplier ID
    @GetMapping("/payOut/invoiceList/{stId}")
    @ApiOperation(value = "Tedarikçiye ait faturalar getirilir.")
    public Map<ERest,Object> invoiceListPayOut(@PathVariable String stId){
        return payOutLayer.invoiceList(stId);
    }

    //PayOut delete
    @DeleteMapping("/payOut/delete/{stId}")
    @ApiOperation(value = "Kasa çıkışı silme işlemi.")
    public Map<ERest,Object> deletePayOut(@PathVariable String stId){
        return payOutLayer.delete(stId);
    }

    //PayOut update
    @PutMapping("/payOut/update")
    @ApiOperation(value = "Kasa çıkışı güncelleme işlemi.")
    public Map<ERest,Object> updatePayOut(@RequestBody @Valid PayOut payOut,BindingResult bindingResult){
        return payOutLayer.update(payOut,bindingResult);
    }

    //PayOut search with elasticsearch
    @GetMapping("/payOut/search/{data}")
    @ApiOperation(value = "Kasa çıkışı arama işlemi.")
    public Map<ERest,Object> searchPayOut(@PathVariable String data){
        return payOutLayer.elasticSearchPayOut(data);
    }

    //PayOut insert all data to elasticsearch database
    @GetMapping("/payOut/elasticInsertData")
    @ApiOperation(value = "Kasa çıkışı hazır bilgileri elasticsearch veri tabanına ekleme.")
    public Map<ERest,Object> elasticInsertDataPayOut(){
        return payOutLayer.elasticInsertDataPayOut();
    }

    //------------------------------------------ PayOut Section - End --------------------------------------------//

}
