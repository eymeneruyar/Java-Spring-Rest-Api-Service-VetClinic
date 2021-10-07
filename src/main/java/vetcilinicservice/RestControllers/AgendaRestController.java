package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.AgendaLayer;
import vetcilinicservice.Entities.Agenda;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/agenda")
@Api(value = "AgendaRestController",authorizations = {@Authorization(value = "basicAuth")})
public class AgendaRestController {

    final AgendaLayer aLayer;
    public AgendaRestController(AgendaLayer aLayer) {
        this.aLayer = aLayer;
    }

    //Agenda add
    @PostMapping("/add")
    @ApiOperation(value = "Ajandaya not ekler.")
    public Map<ERest,Object> add(@RequestBody @Valid Agenda agenda, BindingResult bindingResult){
        return aLayer.add(agenda,bindingResult);
    }

    //Agenda note list with active user
    @GetMapping("/list")
    @ApiOperation(value = "Ajanda notlarını listeler.")
    public Map<ERest,Object> list(){
        return aLayer.list();
    }

    //Agenda delete
    @DeleteMapping("/delete/{stId}")
    @ApiOperation(value = "Seçilen bir notu siler.")
    public Map<ERest,Object> delete(@PathVariable String stId){
        return aLayer.delete(stId);
    }

    //Agenda update
    @PutMapping("/update")
    @ApiOperation(value = "Seçilen bir notu günceller.")
    public Map<ERest,Object> update(@RequestBody @Valid Agenda agenda,BindingResult bindingResult){
        return aLayer.update(agenda,bindingResult);
    }

}
