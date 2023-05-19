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
public class TimeAverages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String averageLiveTimeWorkday;
    private String averageSeedingDurationWorkday;
    private String averageSeedingStartTimeWorkday;
    private String averageLiveTimeWeekend;
    private String averageSeedingDurationWeekend;
    private String averageSeedingStartTimeWeekend;
}
