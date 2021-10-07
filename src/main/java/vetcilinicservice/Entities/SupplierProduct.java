package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ApiModel(value = "Supplier Product Model", description = "Tedarikçiye ait ürün bilgileri saklamaktadır.")
public class SupplierProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sup_pro_ıd", nullable = false)
    private Integer supProId;

    @NotNull(message = "SupProName Null Olamaz")
    @NotEmpty(message = "SupProName Boş Olamaz")
    @ApiModelProperty(value = "Ürün Adı",required = true)
    private String supProName;

    @NotNull(message = "SupProName Null Olamaz")
    @NotEmpty(message = "SupProName Boş Olamaz")
    @ApiModelProperty(value = "Ürün Birimi",required = true)
    private String supProUnit;

    @NotNull(message = "SupProPrice Null Olamaz")
    @NotEmpty(message = "SupProPrice Boş Olamaz")
    @ApiModelProperty(value = "Ürün Fiyatı",required = true)
    private Integer supProPrice;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "supllier_sup_ıd")
    private Supplier supplier;
}
