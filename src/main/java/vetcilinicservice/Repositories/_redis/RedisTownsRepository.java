package vetcilinicservice.Repositories._redis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import vetcilinicservice.Entities.Towns;
import vetcilinicservice.Model.RedisTowns;

import java.util.List;

@EnableRedisRepositories
public interface RedisTownsRepository extends CrudRepository<RedisTowns,String> {

    List<RedisTowns> findByTownCityKeyEquals(Integer townCityKey);

}
