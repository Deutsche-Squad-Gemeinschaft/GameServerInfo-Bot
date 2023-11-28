package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.LastLoggedMatch;

@Repository
public interface LastLoggedMatchRepository extends JpaRepository<LastLoggedMatch, Integer> {
}
