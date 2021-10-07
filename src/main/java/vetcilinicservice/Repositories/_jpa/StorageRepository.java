package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Storage;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage,Integer> {
    List<Storage> findByOrderByStorIdAsc(Pageable pageable);
}
