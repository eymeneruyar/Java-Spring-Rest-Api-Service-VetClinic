package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import vetcilinicservice.Entities.Sales;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales,Integer> {

    @Procedure(name = "procProductUpdateStock")
    void procProductUpdateStock(@Param("id") Integer id, @Param("amount") Integer amount);

    List<Sales> findByCustomer_CuIdEquals(Integer cuId);

    List<Sales> findByProduct_ProIdEquals(Integer proId);

    List<Sales> findByProduct_ProSalesPriceEquals(Integer proSalesPrice);

    List<Sales> findByOrderBySaIdAsc(Pageable pageable);

}
