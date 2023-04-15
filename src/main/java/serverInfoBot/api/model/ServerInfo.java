package serverInfoBot.api.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ServerInfo {

    private String name;
    private int players;
    private String status;
    private String map;
    private int playTime;
    private int pubQueue;
    private int resQueue;
    private String teamOne;
    private String teamTwo;
}
