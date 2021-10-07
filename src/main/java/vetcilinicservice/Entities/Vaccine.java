package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ApiModel(value = "Vaccine Model", description = "Sistemde kayıtlı olan aşı bilgilerini saklamaktadır.")
public class Vaccine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaid", nullable = false)
    private Integer vacid;

    @NotNull(message = "Name Parametresi Null Olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @Length(min = 2, max = 100,message = "Ürün Adı en az 2 Karakter, en çok 100 Karakter Olmalıdır")
    @ApiModelProperty(value = "Aşı Adı",required = true)
    private String vacName;

    @NotNull(message = "Unit Parametresi Null olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @ApiModelProperty(value = "Aşı Birimi",required = true)
    private String vacUnit;

    @NotNull(message = "Category Parametresi Null olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @ApiModelProperty(value = "Aşı Kategori",required = true)
    private String vacCategory;

    @NotNull(message = "Detail Parametresi Null olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @Length(min = 10, max = 300,message = "Ürün Detayı en az 10 Karakter, en çok 300 Karakter Olmalıdır")
    @ApiModelProperty(value = "Aşı Detayı",required = true)
    private String vacDetail;

    @NotNull(message = "Type Parametresi Null olamaz")
    @NotEmpty(message = "Bu alan boş bırakılamaz")
    @ApiModelProperty(value = "Aşı Türü",required = true)
    private String vacType;

    @NotNull(message = "Supplier Parametresi Null olamaz")
    @NotEmpty(message = "Bu alan boş bırakılamaz")
    @ApiModelProperty(value = "Aşı Tedarikçisi",required = true)
    private String vacSupplier;

    @NotNull(message = "Barcode Parametresi Null olamaz")
    @Column(unique = true)
    @ApiModelProperty(value = "Aşı Barkod",required = true)
    private Long vacBarcode;

    @NotNull(message = "Code Parametresi Null olamaz")
    @Column(unique = true)
    @ApiModelProperty(value = "Aşı Kodu",required = true)
    private Long vacCode;

    @NotNull(message = "Tax Parametresi Null olamaz")
    @NotEmpty(message = "Bu alan boş bırakılamaz")
    @ApiModelProperty(value = "Aşı KDV Bilgisi",required = true)
    private String vacTax;

    @NotNull(message = "BuyingPrice Parametresi Null olamaz")
    @Min(value = 1 , message = "Ürün Alış Fiyatı Min 1 olmalıdır.")
    @ApiModelProperty(value = "Aşı Alış Fiyatı",required = true)
    private Integer vacBuyingPrice;

    @NotNull(message = "SalesPrice Parametresi Null olamaz")
    @Min(value = 1 , message = "Ürün Satış Fiyatı Min 1 olmalıdır.")
    @ApiModelProperty(value = "Aşı Satış Fiyatı",required = true)
    private Integer vacSalesPrice;

    @NotNull(message = "Quantity Parametresi Null olamaz")
    @Min(value = 1 , message = "Ürün Miktarı Min 1 olmalıdır.")
    @ApiModelProperty(value = "Aşı Miktarı",required = true)
    private Integer vacQuantity;


}
