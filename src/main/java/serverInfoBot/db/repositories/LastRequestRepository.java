package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.LastRequest;

@Repository
public interface LastRequestRepository extends JpaRepository<LastRequest, Integer> {
    LastRequest findById(int id);
}
