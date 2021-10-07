package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.BuyingPayment;

public interface BuyingPaymentRepository extends JpaRepository<BuyingPayment,Integer> {
    BuyingPayment findByBuying_BuyIdEquals(Integer buyId);
}
