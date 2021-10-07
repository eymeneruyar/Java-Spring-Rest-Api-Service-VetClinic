package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@ApiModel(value = "Customer Model", description = "Müşteri aktiviteleri için kullanılır.")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuId", nullable = false)
    private Integer cuId;

    @NotNull(message = "Müşteri ismi boş olamaz!")
    @NotEmpty(message = "Müşteri ismi boş olamaz!")
    @Length(min = 2,max = 50,message = "Müşteri ismi min 2, max 50 karakter olabilir!")
    @ApiModelProperty(value = "Müşteri Adı",required = true)
    private String cuName;

    @NotNull(message = "Müşteri soyismi boş olamaz!")
    @NotEmpty(message = "Müşteri soyismi boş olamaz!")
    @Length(min = 2,max = 50,message = "Müşteri soyismi min 2, max 50 karakter olabilir!")
    @ApiModelProperty(value = "Müşteri Soyadı",required = true)
    private String cuSurname;

    @NotNull(message = "Müşteri TC/Vergi No boş olamaz!")
    @NotEmpty(message = "Müşteri TC/Vergi No boş olamaz!")
    @Length(min = 2,max = 50,message = "Müşteri TC/Vergi No min 2, max 50 karakter olabilir!")
    @Column(unique = true)
    @ApiModelProperty(value = "Müşteri TC/Vergi No",required = true)
    private String cuTax;

    @ApiModelProperty(value = "Müşteri Vergi Dairesi",required = true)
    private String cuTaxOffice;

    @NotNull(message = "Müşteri telefon numarası boş olamaz!")
    @NotEmpty(message = "Müşteri telefon numarası boş olamaz!")
    @Length(min = 2,max = 11,message = "Müşteri telefon numarası min 2, max 11 karakter olabilir!")
    @Column(unique = true)
    @ApiModelProperty(value = "Müşteri Telefon Numarası",required = true)
    private String cuPhone;

    @ApiModelProperty(value = "Müşteri 2.Telefon Numarası",required = true)
    private String cuPhone2;

    @NotNull(message = "Müşteri E-Mail boş olamaz!")
    @NotEmpty(message = "Müşteri E-Mail boş olamaz!")
    @Length(min = 2,max = 255,message = "Müşteri E-Mail adresi min 2, max 255 karakter olabilir!")
    @Column(unique = true)
    @ApiModelProperty(value = "Müşteri E-Mail Adresi",required = true)
    private String cuEmail;

    @ApiModelProperty(value = "Müşteri Türü (Bireysel/Kurumsal)",required = true)
    private String cuType;

    @NotNull(message = "Müşteri adres bilgileri İL bölülümü boş olamaz!")
    @ApiModelProperty(value = "İl",required = true)
    private int cuCity;

    @NotNull(message = "Müşteri adres bilgileri İLÇE bölülümü boş olamaz!")
    @NotEmpty(message = "Müşteri adres bilgileri İLÇE bölülümü boş olamaz!")
    @Length(min = 2,max = 50,message = "Müşteri adres bilgileri İLÇE bölülümü min 2, max 50 karakter olabilir!")
    @ApiModelProperty(value = "İlçe",required = true)
    private String cuTown;

    @NotNull(message = "Müşteri adres bilgileri ADRES bölülümü boş olamaz!")
    @NotEmpty(message = "Müşteri adres bilgileri ADRES bölülümü boş olamaz!")
    @Length(min = 2,max = 255,message = "Müşteri adres bilgileri ADRES bölülümü min 2, max 255 karakter olabilir!")
    @ApiModelProperty(value = "Müşterinin Adresi",required = true)
    private String cuAddress;

    @ApiModelProperty(value = "Müşteri Notu",required = true)
    private String cuNote;

}
