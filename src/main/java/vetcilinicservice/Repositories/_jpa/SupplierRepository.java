package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Supplier;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier,Integer> {

    List<Supplier> findByOrderBySupIdAsc(Pageable pageable);

}
