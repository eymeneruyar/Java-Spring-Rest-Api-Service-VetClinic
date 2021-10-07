package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@ApiModel(value = "Schedule Calendar Model", description = "Randevu takviminde oluşturulan randevu bilgileri saklamaktadır.")
public class ScheduleCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid", nullable = false)
    private Integer sid;

    private String id;

    @ApiModelProperty(value = "Randevu Başlığı",required = true)
    private String title;

    @ApiModelProperty(value = "Randevu Süresi (Tam Gün)",required = true)
    private Boolean isAllDay;

    @ApiModelProperty(value = "Randevu Başlangıç Tarihi",required = true)
    private String start;

    @ApiModelProperty(value = "Randevu Bitiş Tarihi",required = true)
    private String end;

    @ApiModelProperty(value = "Randevu Kategorisi",required = true)
    private String category;

    private String dueDateClass;

    @ApiModelProperty(value = "Randevu Rengi",required = true)
    private String color;

    @ApiModelProperty(value = "Randevu Rengi",required = true)
    private String bgColor;

    @ApiModelProperty(value = "Randevu Rengi",required = true)
    private String dragBgColor;

    @ApiModelProperty(value = "Randevu Rengi",required = true)
    private String borderColor;

    @ApiModelProperty(value = "Randevu Yeri",required = true)
    private String location;

    private String raw;

    @ApiModelProperty(value = "Randevu Durumu",required = true)
    private String state;
    private String calendarId;

}
