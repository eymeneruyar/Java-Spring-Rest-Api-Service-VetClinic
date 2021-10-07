package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Vaccine;

import java.util.List;

public interface VaccineRepository extends JpaRepository<Vaccine,Integer> {
    List<Vaccine> findByOrderByVacidAsc(Pageable pageable);
}
