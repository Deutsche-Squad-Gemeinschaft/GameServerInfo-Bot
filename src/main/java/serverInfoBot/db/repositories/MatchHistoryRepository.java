package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.MatchHistory;

@Repository
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Integer> {
}
