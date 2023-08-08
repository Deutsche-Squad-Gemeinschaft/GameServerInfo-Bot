package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import serverInfoBot.db.entities.LastLoggedMatch;

public interface LastLoggedMatchRepository extends JpaRepository<LastLoggedMatch, Integer> {
}
