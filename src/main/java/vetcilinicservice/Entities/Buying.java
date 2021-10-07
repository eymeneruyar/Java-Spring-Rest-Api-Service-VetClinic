package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ApiModel(value = "Buying Model", description = "Firma tarafından satın alınan ürünlerin bilgilerini saklar.")
public class Buying {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "buy_ıd", nullable = false)
    private Integer buyId;

    @NotNull(message = "Fatura No Null olamaz!")
    @Column(unique = true)
    @ApiModelProperty(value = "Fatura NUmarası",required = true)
    private Long buyReceiptNo;

    @NotNull(message = "Miktar Null Olamaz")
    @NotEmpty(message = "Miktar Boş Olamaz")
    @Length(min = 1, max = 5, message = "Miktar min 1, max 5 haneli olabilir. ")
    @ApiModelProperty(value = "Satın Alınan Miktar",required = true)
    private String buyAmount;

    @ApiModelProperty(value = "Detay Bilgisi",required = true)
    private String buyNote;

    @ApiModelProperty(value = "Satın Alınan Ürünün Birimi",required = true)
    private String buyUnit;

    @ApiModelProperty(value = "Alış Tarihi",required = true)
    private String buyDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "supplierProductId")
    private SupplierProduct supplierProduct;


}
