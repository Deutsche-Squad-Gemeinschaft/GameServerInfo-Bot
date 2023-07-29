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
public class LastRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String serverName;
    private int players;
    private String status;
    private String layer;
    private int totalQueue;
    private String playtime;
    private String teamOne;
    private String teamTwo;
    private String flag;
    private String date;
    private String time;
}
