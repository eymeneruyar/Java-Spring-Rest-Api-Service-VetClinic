package vetcilinicservice.Repositories._redis;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import vetcilinicservice.Model.RedisCities;

@EnableRedisRepositories
public interface RedisCitiesRepository extends CrudRepository<RedisCities,String> {
}
