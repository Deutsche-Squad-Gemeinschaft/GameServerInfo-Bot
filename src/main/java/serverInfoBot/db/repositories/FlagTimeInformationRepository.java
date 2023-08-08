package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.FlagTimeInformation;

import java.util.List;

@Repository
public interface FlagTimeInformationRepository extends JpaRepository<FlagTimeInformation, Integer> {
    FlagTimeInformation findByDate(String date);
    List<FlagTimeInformation> findByWeekday(String weekday);
}
