package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.MatchHistory;

import java.util.List;

@Repository
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Integer> {
    MatchHistory findByDateTime(String dateTime);

    List<MatchHistory> findByStartDateAndFlag(String date, String flag);

    List<MatchHistory> findByFlag(String flag);
}
