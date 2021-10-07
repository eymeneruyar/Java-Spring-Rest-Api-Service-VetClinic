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
@ApiModel(value = "Agenda Model", description = "Sisteme giriş yapan kullanıcının not bilgilerini saklar.")
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agendaId", nullable = false)
    private Integer agendaId;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @Length(min = 1,max = 250,message = "Bu alana min 1, max 250 karakter girilmelidir!")
    @ApiModelProperty(value = "Not Başlığı",required = true)
    private String agendaTitle;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @ApiModelProperty(value = "Planlanan Tarih",required = true)
    private String agendaDate;

    @NotNull(message = "Bu alan boş olamaz!")
    @NotEmpty(message = "Bu alan boş olamaz!")
    @Length(min = 1,max = 1000,message = "Bu alana min 1, max 1000 karakter girilmelidir!")
    @ApiModelProperty(value = "Not",required = true)
    private String agendaNote;

    @ApiModelProperty(value = "Not Kayıt Tarihi",required = true)
    private String saveDate;

    @ApiModelProperty(value = "Not Kaydı Yapan Kulanıcının ID Numarası",required = true)
    private Integer uid;

}
