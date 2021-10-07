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
@ApiModel(value = "Treatment Model", description = "Muayene bilgilerini saklamaktadır")
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tre_ıd", nullable = false)
    private Integer treId;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @Length(min = 2, max = 200)
    @ApiModelProperty(value = "Muayene Notları",required = true)
    private String treNote;

    @ApiModelProperty(value = "Muayene Laboratuvar Bilgisi",required = true)
    private String treLab;

    @ApiModelProperty(value = "Muayene Operasyon Bilgisi",required = true)
    private String treOperation;

    @ApiModelProperty(value = "Muayene Pansuman Bilgisi",required = true)
    private String treDressing;

    @ApiModelProperty(value = "Muayene Görüntüleme Bilgisi",required = true)
    private String treRadiography;

    @ApiModelProperty(value = "Muayene İlaç Bilgisi",required = true)
    private String treMedicine;

    @NotNull(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Muayene Kodu",required = true)
    private Long treCode;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "patientId")
    private Patient patient;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "vaccineId")
    private Vaccine vaccine;

}
