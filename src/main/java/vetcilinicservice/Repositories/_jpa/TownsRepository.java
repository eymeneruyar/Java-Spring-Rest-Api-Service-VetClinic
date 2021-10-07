package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Towns;

import java.util.List;

public interface TownsRepository extends JpaRepository<Towns,Integer> {

    List<Towns> findByTownCityKeyEquals(Integer townCityKey);

}
