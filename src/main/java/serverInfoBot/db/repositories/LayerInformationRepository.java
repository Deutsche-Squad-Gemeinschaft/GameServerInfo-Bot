package serverInfoBot.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import serverInfoBot.db.entities.LayerInformation;

@Repository
public interface LayerInformationRepository extends JpaRepository<LayerInformation, Integer> {

    LayerInformation findByCurrentLayerName(String currentLayerName);
    LayerInformation findByNextLayerName(String nextLayerName);
}
