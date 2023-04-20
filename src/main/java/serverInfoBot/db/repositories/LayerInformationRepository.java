package serverInfoBot.db.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.LayerInformation;

import java.util.List;

@Repository
public interface LayerInformationRepository extends JpaRepository<LayerInformation, Integer> {

    LayerInformation findByLayerName(String layerName);
}
