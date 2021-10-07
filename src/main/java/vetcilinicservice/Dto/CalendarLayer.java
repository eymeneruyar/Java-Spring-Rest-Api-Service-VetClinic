package vetcilinicservice.Dto;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vetcilinicservice.Entities.ScheduleCalendar;
import vetcilinicservice.Repositories._jpa.CalendarInfoRepository;
import vetcilinicservice.Repositories._jpa.ScheduleCalendarRepository;
import vetcilinicservice.Utils.ERest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CalendarLayer {

    final CalendarInfoRepository cInfo;
    final ScheduleCalendarRepository sRepo;
    public CalendarLayer(CalendarInfoRepository cInfo, ScheduleCalendarRepository sRepo) {
        this.cInfo = cInfo;
        this.sRepo = sRepo;
    }

    //All calendar info
    public Map<String, Object> calendarInfo() {
        Map<String, Object> hm = new LinkedHashMap<>();
        hm.put("calendarInfos", cInfo.findAll());
        return hm;
    }

    public Map<String, Object> calendarInfo(ScheduleCalendar scheduleCalendar) {
        Map<String, Object> hm = new LinkedHashMap<>();
        Optional<ScheduleCalendar> isThere = sRepo.findScheduleId(scheduleCalendar.getId());
        if (isThere.isPresent()) {
            scheduleCalendar.setSid(isThere.get().getSid());
        }
        ScheduleCalendar s = sRepo.saveAndFlush(scheduleCalendar);
        hm.put("scheduleCalendar", s);
        return hm;
    }

    //List Schedule
    public Map<String, Object> listSchedule(String calendarId) {
        Map<String, Object> hm = new LinkedHashMap<>();
        List<ScheduleCalendar> s = sRepo.findByCalendarIdEquals(calendarId);
        hm.put("listSchedule", s);
        return hm;
    }

    //Delete Schedule
    public Map<ERest, Object> deleteSchedule(String stSid) {
        Map<ERest, Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stSid);
            Optional<ScheduleCalendar> optSchedule = sRepo.findById(id);
            if (optSchedule.isPresent()) {
                ScheduleCalendar sc = optSchedule.get();
                sRepo.deleteById(sc.getSid());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,sc);
            }else {
                hm.put(ERest.status,false);
                hm.put(ERest.message,"Silmek istenen randevu bulunamadı!");
            }
        } catch (NumberFormatException e) {
            hm.put(ERest.status,false);
            hm.put(ERest.message,"Silme işlemi sırasında bir hata oluştu!");
        }
        return hm;
    }

}
