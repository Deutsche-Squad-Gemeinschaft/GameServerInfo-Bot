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
public class MatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String duration;
    private String layerName;
    private String flag;
    private String dateTime;
    private String mapName;
}
