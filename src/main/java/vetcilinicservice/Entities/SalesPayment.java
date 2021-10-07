package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@ApiModel(value = "Sales Payment Model", description = "Müşteriye yapılan satışların borç bilgilerini saklamaktadır.")
public class SalesPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sa_pa_ıd", nullable = false)
    private Integer saPaId;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "salesId")
    private Sales sales;

    @ApiModelProperty(value = "Satış Toplam Borç",required = true)
    private Integer TotalDebt;

    @ApiModelProperty(value = "Satış Kalan Borç",required = true)
    private Integer RemainDebt;

}
