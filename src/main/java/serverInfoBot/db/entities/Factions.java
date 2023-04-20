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
public class Factions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String factionShort;
    private String factionLong;
}
