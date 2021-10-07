package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@ApiModel(value = "Cities Model", description = "Türkiyede bulunan şehirlerin bilgisi saklanmaktadır.")
public class Cities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sehir_id", nullable = false)
    private Integer id;

    @Column(name = "sehir_title")
    @ApiModelProperty(value = "Şehir Adı",required = true)
    private String name;

    @Column(name = "sehir_key")
    @ApiModelProperty(value = "Şehir Plaka Numarası",required = true)
    private int cityKey;


}
