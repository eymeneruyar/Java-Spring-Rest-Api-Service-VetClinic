package vetcilinicservice.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("cities")
public class RedisCities {

    @Id
    private String stId;

    private Integer id;
    private String name;
    private int cityKey;

}
