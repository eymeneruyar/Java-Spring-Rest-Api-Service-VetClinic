package vetcilinicservice.Repositories._jpa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Buying;

import java.util.List;

public interface BuyingRepository extends JpaRepository<Buying,Integer> {

    List<Buying> findBySupplier_SupIdEquals(Integer supId);

    List<Buying> findByOrderByBuyIdAsc(Pageable pageable);

}
