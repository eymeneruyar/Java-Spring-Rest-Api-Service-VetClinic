package vetcilinicservice.Entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@ApiModel(value = "Account Activities", description = "Hesap haraketlerini kontrol etmektedir. Hesaba giriş yapan kişinin bilgilerini içerir.")
public class AccountActivities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(value = "Kullanıcı Adı",required = true)
    private String nameInfo;

    @ApiModelProperty(value = "Kullanıcı Soyadı",required = true)
    private String surnameInfo;

    @ApiModelProperty(value = "Kullanıcı E-Mail",required = true)
    private String emailInfo;

    @ApiModelProperty(value = "Kullanıcı Session Bilgisi",required = true)
    private String sessionInfo;

    @ApiModelProperty(value = "Kullanıcı Ip Bilgisi",required = true)
    private String ipInfo;

    @ApiModelProperty(value = "Kullanıcı Rol Bilgisi",required = true)
    private String roleInfo;

    @ApiModelProperty(value = "Kullanıcının bulunduğu Domain Adresi",required = true)
    private String urlInfo;

    @ApiModelProperty(value = "Kullanıcı Giriş Tarihi",required = true)
    private String dateInfo;

    @ApiModelProperty(value = "Kullanıcı Profil Resmi",required = true)
    private String imageFile;

    public AccountActivities() {}

    public AccountActivities(String nameInfo, String surnameInfo, String emailInfo, String sessionInfo, String ipInfo, String roleInfo, String urlInfo, String dateInfo) {
        this.nameInfo = nameInfo;
        this.surnameInfo = surnameInfo;
        this.emailInfo = emailInfo;
        this.sessionInfo = sessionInfo;
        this.ipInfo = ipInfo;
        this.roleInfo = roleInfo;
        this.urlInfo = urlInfo;
        this.dateInfo = dateInfo;
    }

}
