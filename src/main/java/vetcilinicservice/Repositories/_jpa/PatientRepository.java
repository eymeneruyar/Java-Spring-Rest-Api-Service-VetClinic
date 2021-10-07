package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Patient;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient,Integer> {

    List<Patient> findByCustomer_CuIdEquals(Integer cuId);

    List<Patient> findBySaveDateEqualsIgnoreCase(String saveDate);

    List<Patient> findByOrderByPaIdAsc(Pageable pageable);

}
