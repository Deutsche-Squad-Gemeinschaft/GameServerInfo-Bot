package serverInfoBot.service;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.api.BattlemetricsController;
import serverInfoBot.api.model.ServerInfo;


import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Service
public class BattlemetricsService {

    private BattlemetricsController battlemetricsController;
    private SquadData squadData;

    @Autowired
    public BattlemetricsService(BattlemetricsController battlemetricsController, SquadData squadData) {
        this.battlemetricsController = battlemetricsController;
        this.squadData = squadData;
    }

    public EmbedBuilder getServerInfo() {
        ServerInfo serverInfo = battlemetricsController.getData();

        int totalQueue = calcQueue(serverInfo.getPubQueue(), serverInfo.getResQueue());
        String playTime = parsePlayTime(serverInfo.getPlayTime());
        String teamOne = parseTeamName(serverInfo.getTeamOne());
        String teamTwo = parseTeamName(serverInfo.getTeamTwo());
        String map = serverInfo.getMap();
        String mapImage = parseMapName(map);

        return createEmbedServerInfo(serverInfo.getName(), serverInfo.getPlayers(), serverInfo.getStatus(), map, totalQueue, playTime, teamOne, teamTwo, mapImage);
    }

    private EmbedBuilder createEmbedServerInfo(String name, int players, String status, String map, int totalQueue, String playTime, String teamOne, String teamTwo, String mapImage) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(name, null);

        eb.setColor(new Color(255, 196, 12));

        eb.addField(":flag_de: Status:", status, true);
        eb.addField(":busts_in_silhouette: Spielerzahl:", players + " +" + totalQueue + " in Queue", true);
        eb.addField(":map: Map:", map, true);
        eb.addBlankField(true);
        eb.addField(":clock10: Rundenzeit:", playTime, true);
        eb.addField(":flag_white: Fraktionen:", teamOne + " vs " + teamTwo, true);
        eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
        eb.setImage(mapImage);
        eb.setTimestamp(Instant.now());
        return eb;
    }

    private int calcQueue(int pubQueue, int resQueue) {
        return pubQueue + resQueue;
    }

    private String parsePlayTime(int playTime) {
        return String.format("%02d:%02d:%02d", playTime / 3600, (playTime % 3600) / 60, (playTime % 60));
    }

    private String parseTeamName(String teamName){
        List<String> validTeamNames = squadData.getValidTeamNames();

        teamName = teamName.toUpperCase();
        for (String factionName : validTeamNames) {
            if (teamName.contains(factionName)) {
                return factionName;
            }
        }
        return teamName;
    }

    private String parseMapName(String mapName){

        List<ServiceMaps> mapData = squadData.getMapData();

        mapName = mapName.toUpperCase();
        for (int i = 0; i < mapData.size(); i++){
           ServiceMaps serviceMaps = mapData.get(i);
            if (mapName.contains(serviceMaps.getMapName())) {
                return serviceMaps.getImageLink();
            }
        }
        return "https://i.imgur.com/ucy7Jzf.png";
    }
}
