package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@ApiModel(value = "PayIn Model", description = "Kasa Giriş bilgileri saklamaktadır.")
public class PayIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pin_ıd", nullable = false)
    private Integer pinId;

    @ApiModelProperty(value = "Kasa Giriş Notları",required = true)
    private String pinNote;

    @ApiModelProperty(value = "Kasa Giriş Ödeme Miktarı (₺)",required = true)
    private Integer pinAmount;

    @ApiModelProperty(value = "Kasa Giriş Ödeme Türü",required = true)
    private String pinPayType;

    @ApiModelProperty(value = "Kasa Giriş Tarihi",required = true)
    private String createdDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "salesId")
    private Sales sales;

}
