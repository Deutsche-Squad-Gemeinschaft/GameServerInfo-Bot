package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.Settings;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {
    Settings findById(int id);
}
