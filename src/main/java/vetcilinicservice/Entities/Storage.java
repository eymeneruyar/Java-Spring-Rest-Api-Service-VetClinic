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
@ApiModel(value = "Storage Model", description = "Depo bilgilerini saklamaktadır.")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storId", nullable = false)
    private Integer storId;

    @NotNull(message = "Depo ismi boş olamaz!")
    @NotEmpty(message = "Depo ismi boş olamaz!")
    @Length(min = 1,max = 100,message = "Depo ismi min 1, max 100 karakter olabilir!")
    @ApiModelProperty(value = "Depo Adı",required = true)
    private String storName;

    @NotNull(message = "Depo kodu boş olamaz!")
    @NotEmpty(message = "Depo kodu boş olamaz!")
    @Length(min = 1,max = 25,message = "Depo kodu min 1, max 25 karakter olabilir!")
    @ApiModelProperty(value = "Depo Numarası",required = true)
    private String storNo;

    @NotNull(message = "Depo durumu boş olamaz!")
    @NotEmpty(message = "Depo durumu boş olamaz!")
    @Length(min = 1,max = 25,message = "Depo durumu min 1, max 50 karakter olabilir!")
    @ApiModelProperty(value = "Depo Durumu",required = true)
    private String storStatus;



}
