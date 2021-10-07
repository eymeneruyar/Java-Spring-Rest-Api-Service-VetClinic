package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@ApiModel(value = "Buying Payment Model", description = "Firmanın borç bilgilerini saklar.")
public class BuyingPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bu_id", nullable = false)
    private Integer buId;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "buyingId")
    private Buying buying;

    @ApiModelProperty(value = "Ödenecek Toplam Borç",required = true)
    private Integer TotalDebt;

    @ApiModelProperty(value = "Ödenecek Kalan Borç",required = true)
    private Integer RemainDebt;

}
