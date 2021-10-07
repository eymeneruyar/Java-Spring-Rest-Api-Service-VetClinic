package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@ApiModel(value = "Patient Model", description = "Hasta bilgileri saklamaktadır.")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paId", nullable = false)
    private Integer paId;

    @NotNull(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Müşteri Id Numarası",required = true)
    private Integer customerId;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @Length(min = 3,max = 50,message = "İsim min 3, max 50 karakter olabilir!")
    @ApiModelProperty(value = "Hasta Adı",required = true)
    private String paName;

    @Column(unique = true)
    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @Length(min = 3,max = 20,message = "Çip Numarası min 3, max 20 karakter olabilir!")
    @ApiModelProperty(value = "Hasta Çip Numarası",required = true)
    private String paChipNo;

    @Column(unique = true)
    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @Length(min = 3,max = 20,message = "Karne/Küphe numarası min 3, max 20 karakter olabilir!")
    @ApiModelProperty(value = "Hasta Karne/Küpe Numarası",required = true)
    private String paAirTagNo;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Hasta Doğum Tarihi",required = true)
    private String paBirthDate;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Hasta Türü",required = true)
    private String paType; //Tür

    @ApiModelProperty(value = "Hasta Kısırlaştırma Durumu",required = true)
    private String paSpay; //Kısırlaştırılmış on/off

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Hasta Cinsi",required = true)
    private String paKind; //Cins

    @ApiModelProperty(value = "Hasta Rengi",required = true)
    private String paColor;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Hasta Cinsiyeti",required = true)
    private String paSexType; //Cinsiyet

    @ApiModelProperty(value = "Hasta Kayıt Tarihi",required = true)
    private String saveDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "customer_cu_ıd")
    private Customer customer;

}
