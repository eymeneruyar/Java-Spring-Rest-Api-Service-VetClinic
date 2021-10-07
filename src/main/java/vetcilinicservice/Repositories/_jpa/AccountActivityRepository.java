package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.AccountActivities;

public interface AccountActivityRepository extends JpaRepository<AccountActivities,Integer> {
}
