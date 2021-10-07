package vetcilinicservice.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("towns")
public class RedisTowns {

    @Id
    private String stId;
    private Integer id;
    private String name;
    private Integer townKey;
    @Indexed //Bu notasyon olmadan plaka numarasına göre ilçe getirme yapılamıyor.
    private Integer townCityKey;

}
