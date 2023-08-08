package serverInfoBot.db.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.Factions;

import java.util.List;

@Repository
public interface FactionsRepository extends JpaRepository<Factions, Integer> {
    @NotNull
    List<Factions> findAll();

    Factions findByFactionLong(String factionLong);
}
