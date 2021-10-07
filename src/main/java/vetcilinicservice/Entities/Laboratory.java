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
@ApiModel(value = "Laboratory Model", description = "Laboratuvar Bilgileri Saklar.")
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "labId", nullable = false)
    private Integer labId;

    @NotNull(message = "Hasta sahibi boş olamaz!")
    @ApiModelProperty(value = "Müşteri Id Numarası",required = true)
    private Integer labCuId;

    @NotNull(message = "Laboratuvar türü boş olamaz!")
    @NotEmpty(message = "Laboratuvar türü boş olamaz!")
    @ApiModelProperty(value = "Laboratuvar Sonuç Türü",required = true)
    private String labType;

    @ApiModelProperty(value = "Laboratuvar Sonucu Resim Dosyası",required = true)
    private String labFileName;

    @NotNull(message = "Teşhis boş olamaz!")
    @NotEmpty(message = "Teşhis boş olamaz!")
    @Length(min = 1,message = "Teşhis alanı min 1 karakter almalıdır!")
    @Column(columnDefinition = "text")
    @ApiModelProperty(value = "Laboratuvar Sonucu Notları",required = true)
    private String labNote;

    @NotNull(message = "Hasta boş olamaz!")
    @ApiModelProperty(value = "Hasta Id Numarası",required = true)
    private Integer labPaId;

    @ApiModelProperty(value = "Laboratuvar Sonucu Tarihi",required = true)
    private String labDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "patient_pa_ıd")
    private Patient patient;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "customer_cu_ıd")
    private Customer customer;


}
