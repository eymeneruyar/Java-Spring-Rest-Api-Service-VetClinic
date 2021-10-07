package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@ApiModel(value = "Cities Model", description = "Türkiyede bulunan şehirlerin ilçe bilgilerini saklamaktadır.")
public class Towns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ilce_id", nullable = false)
    private Integer id;

    @Column(name = "ilce_title")
    @ApiModelProperty(value = "İlçe Adı",required = true)
    private String name;

    @Column(name = "ilce_key")
    @ApiModelProperty(value = "İlçe Numarası",required = true)
    private Integer townKey;

    @Column(name = "ilce_sehirkey")
    @ApiModelProperty(value = "İlçe Plaka Kodu",required = true)
    private Integer townCityKey;


}
