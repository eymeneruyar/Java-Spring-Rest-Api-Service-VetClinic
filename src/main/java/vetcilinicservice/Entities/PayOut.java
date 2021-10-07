package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@ApiModel(value = "PayOut Model", description = "Kasa Çıkış Bilgileri Saklamaktadır.")
public class PayOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_ıd", nullable = false)
    private Integer poId;

    @ApiModelProperty(value = "Kasa Çıkış Notları",required = true)
    private String poutNote;

    @ApiModelProperty(value = "Kasa Çıkış Ödeme Miktarı (₺)",required = true)
    private Integer poutAmount;

    @ApiModelProperty(value = "Kasa Çıkış Ödeme Türü",required = true)
    private String poutPayType;

    @ApiModelProperty(value = "Kasa Çıkış Tarihi",required = true)
    private String createdDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "buyingId")
    private Buying buying;
}
