package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.TimeAverages;

@Repository
public interface TimeAveragesRepository extends JpaRepository<TimeAverages, Integer> {
}
