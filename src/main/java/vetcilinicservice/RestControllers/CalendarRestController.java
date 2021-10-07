package vetcilinicservice.RestControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.*;
import vetcilinicservice.Dto.CalendarLayer;
import vetcilinicservice.Entities.ScheduleCalendar;
import vetcilinicservice.Utils.ERest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/calendar")
@Api(value = "CalendarRestController",authorizations = {@Authorization(value = "basicAuth")})
public class CalendarRestController {

    final CalendarLayer calLayer;
    public CalendarRestController(CalendarLayer calLayer) {
        this.calLayer = calLayer;
    }

    @GetMapping("/calendarInfo")
    public Map<String, Object> calendarInfo(){
        return calLayer.calendarInfo();
    }

    @PostMapping("/addSchedule")
    @ApiOperation(value = "Randevu ekler.")
    public Map<String, Object> calendarInfo(@RequestBody ScheduleCalendar scheduleCalendar) {
        return calLayer.calendarInfo(scheduleCalendar);
    }

    @GetMapping("/listSchedule/{calendarId}")
    @ApiOperation(value = "Randevuları sıralar.")
    public Map<String, Object> listSchedule(@PathVariable String calendarId) {
        return calLayer.listSchedule(calendarId);
    }

    @DeleteMapping("/deleteSchedule/{stSid}")
    @ApiOperation(value = "Randevu silme işlemini yapar.")
    public Map<ERest, Object> deleteSchedule(@PathVariable String stSid) {
        return calLayer.deleteSchedule(stSid);
    }

}
