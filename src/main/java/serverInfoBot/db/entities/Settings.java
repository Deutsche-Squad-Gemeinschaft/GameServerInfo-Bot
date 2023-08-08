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
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String jgkpTextChannelId;
    private String jgkpMessageId;
    private String jgkpGuildId;
    private String dsgTextChannelId;
    private String dsgMessageId;
    private String dsgGuildId;
    private String testTextChannelId;
    private String testMessageId;
    private String testGuildId;
}
