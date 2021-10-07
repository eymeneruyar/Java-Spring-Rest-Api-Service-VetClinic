package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@ApiModel(value = "Product Model", description = "Ürün bilgilerini saklamaktadır.")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="proId",nullable = false)
    private Integer proId;

    @NotNull(message = "Name Parametresi Null Olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @Length(min = 2, max = 100,message = "Ürün Adı en az 2 Karakter, en çok 100 Karakter Olmalıdır")
    @ApiModelProperty(value = "Ürün Adı",required = true)
    private String proName;

    @NotNull(message = "Unit Parametresi Null olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @ApiModelProperty(value = "Ürün Birimi",required = true)
    private String proUnit;

    @NotNull(message = "Category Parametresi Null olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @ApiModelProperty(value = "Ürün Kategorisi",required = true)
    private String proCategory;

    @NotNull(message = "Detail Parametresi Null olamaz")
    @NotEmpty(message = "Bu Alan Boş Bırakılamaz")
    @Length(min = 10, max = 300,message = "Ürün Detayı en az 10 Karakter, en çok 300 Karakter Olmalıdır")
    @ApiModelProperty(value = "Ürün Detayı",required = true)
    private String proDetail;

    @NotNull(message = "Type Parametresi Null olamaz")
    @NotEmpty(message = "Bu alan boş bırakılamaz")
    @ApiModelProperty(value = "Ürün Türü",required = true)
    private String proType;

    @NotNull(message = "Supplier Parametresi Null olamaz")
    @NotEmpty(message = "Bu alan boş bırakılamaz")
    @ApiModelProperty(value = "Ürün Tedarikçisi",required = true)
    private String proSupplier;

    @NotNull(message = "Barcode Parametresi Null olamaz")
    @Column(unique = true)
    @ApiModelProperty(value = "Ürün Barkod",required = true)
    private Integer proBarcode;

    @NotNull(message = "Code Parametresi Null olamaz")
    @Column(unique = true)
    @ApiModelProperty(value = "Ürün Kodu",required = true)
    private Integer proCode;

    @NotNull(message = "Tax Parametresi Null olamaz")
    @NotEmpty(message = "Bu alan boş bırakılamaz")
    private String proTax;

    @NotNull(message = "BuyingPrice Parametresi Null olamaz")
    @Min(value = 1 , message = "Ürün Alış Fiyatı Min 1 olmalıdır.")
    private Integer proBuyingPrice;

    @NotNull(message = "SalesPrice Parametresi Null olamaz")
    @Min(value = 1 , message = "Ürün Satış Fiyatı Min 1 olmalıdır.")
    private Integer proSalesPrice;

    @NotNull(message = "Quantity Parametresi Null olamaz")
    @Min(value = 1 , message = "Ürün Miktarı Min 1 olmalıdır.")
    private Integer proQuantity;



}
