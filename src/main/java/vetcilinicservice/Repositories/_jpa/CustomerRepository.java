package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {

    List<Customer> findByOrderByCuIdAsc(Pageable pageable);

}
