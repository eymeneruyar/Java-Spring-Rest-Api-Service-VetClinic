package vetcilinicservice.Repositories._jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vetcilinicservice.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByuEmailEqualsIgnoreCase(String uEmail);

    @Query(
            value = "SELECT * FROM USER ORDER BY u_id",
            countQuery = "SELECT count(*) FROM User",
            nativeQuery = true)
    Page<User> findAllUsersWithPagination(Pageable pageable);

}
