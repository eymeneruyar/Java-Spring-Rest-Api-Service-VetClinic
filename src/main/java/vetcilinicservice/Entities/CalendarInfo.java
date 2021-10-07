package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@ApiModel(value = "Calendar Information Model", description = "Randevu Takvimi Seçeneklerini saklar.")
public class CalendarInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Integer cid;

    @ApiModelProperty(value = "Randevu Seçeneği Adı",required = true)
    private String cname;

    @ApiModelProperty(value = "Randevu Seçeneği Rengi",required = true)
    private String ccolor;

    @ApiModelProperty(value = "Randevu Seçeneği Rengi",required = true)
    private String cbgColor;

    @ApiModelProperty(value = "Randevu Seçeneği Rengi",required = true)
    private String cdragBgColor;

    @ApiModelProperty(value = "Randevu Seçeneği Rengi",required = true)
    private String cborderColor;

}
