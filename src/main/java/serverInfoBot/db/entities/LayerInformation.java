package serverInfoBot.db.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class LayerInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String currentLayerName;
    private String nextLayerName;
    private String mapName;
    private String mapImageLink;
    private String teamOne;
    private String teamTwo;
    private String squadmapsLink;
    private String squadlanesLink;
}
