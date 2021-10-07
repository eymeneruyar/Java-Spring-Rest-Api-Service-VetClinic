package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.SalesPayment;

public interface SalesPaymentRepository extends JpaRepository<SalesPayment,Integer> {
    SalesPayment findBySales_SaIdEquals(Integer saId);

}
