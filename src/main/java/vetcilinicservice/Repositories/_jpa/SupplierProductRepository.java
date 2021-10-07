package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.SupplierProduct;

import java.util.List;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct,Integer> {
    List<SupplierProduct> findBySupplier_SupIdEquals(Integer supId);
}
