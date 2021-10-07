package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ApiModel(value = "Sales Model", description = "Satış Bilgilerini Saklamaktadır.")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sa_ıd", nullable = false)
    private Integer saId;

    @ApiModelProperty(value = "Ürün Satış Miktarı",required = true)
    private String saPrAmount;

    @ApiModelProperty(value = "Aşı Satış Miktarı",required = true)
    private String saVacAmount;

    @ApiModelProperty(value = "Satış Notu",required = true)
    private String saNote;

    @ApiModelProperty(value = "Satış Ödeme Türü",required = true)
    private String saPayType;

    @ApiModelProperty(value = "Satış Laboratuvar Türü",required = true)
    private String saLabType;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Satış Fatura Numarası",required = true)
    private String saReceiptNo;

    @ApiModelProperty(value = "Satış Tarihi",required = true)
    private String saSoldDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "patientId")
    private Patient patient;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "vaccineId")
    private Vaccine vaccine;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "productId")
    private Product product;
}
