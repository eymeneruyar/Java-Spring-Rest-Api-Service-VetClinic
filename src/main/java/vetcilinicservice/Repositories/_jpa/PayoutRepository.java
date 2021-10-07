package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.PayOut;

public interface PayoutRepository extends JpaRepository<PayOut,Integer> {
}
