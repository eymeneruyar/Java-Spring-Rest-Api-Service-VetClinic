package vetcilinicservice.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@ApiModel(value = "Role Model", description = "Kullanıcı rol bilgilerini saklamaktadır.")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rid", nullable = false)
    private Integer rid;

    @ApiModelProperty(value = "Rol Adı",required = true)
    private String rName;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.DETACH)
    private List<vetcilinicservice.Entities.User> users;

    @Override
    public String toString() {
        return String.format(" ");
    }

}
