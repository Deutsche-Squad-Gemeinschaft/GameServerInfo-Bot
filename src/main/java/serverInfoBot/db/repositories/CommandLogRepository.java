package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.CommandLog;

@Repository
public interface CommandLogRepository extends JpaRepository<CommandLog, Integer> {
}
