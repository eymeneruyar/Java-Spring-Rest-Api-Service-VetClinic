package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Treatment;

import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment,Integer> {

    List<Treatment> findByOrderByTreIdAsc(Pageable pageable);

}
