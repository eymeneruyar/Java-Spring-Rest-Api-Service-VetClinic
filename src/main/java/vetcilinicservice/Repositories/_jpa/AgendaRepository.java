package vetcilinicservice.Repositories._jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vetcilinicservice.Entities.Agenda;

import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda,Integer> {

    List<Agenda> findByUidEquals(Integer uid);

    List<Agenda> findByUid(Integer uid);

}
