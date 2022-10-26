package serverInfoBot.service;

import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.api.BattlemetricsController;
import serverInfoBot.api.model.ServerInfo;

import java.awt.*;
import java.util.ArrayList;

@Service
public class BattlemetricsService {

    private BattlemetricsController battlemetricsController;

    @Autowired
    public BattlemetricsService(BattlemetricsController battlemetricsController) {
        this.battlemetricsController = battlemetricsController;
    }

    public EmbedBuilder getServerInfo() {
        ServerInfo serverInfo = battlemetricsController.getData();

        int totalQueue = calcQueue(serverInfo.getPubQueue(), serverInfo.getResQueue());
        String playTime = parsePlayTime(serverInfo.getPlayTime());
        String teamOne = parseTeamName(serverInfo.getTeamOne());
        String teamTwo = parseTeamName(serverInfo.getTeamTwo());

        return createEmbedServerInfo(serverInfo.getName(), serverInfo.getPlayers(), serverInfo.getStatus(), serverInfo.getMap(), totalQueue, playTime, teamOne, teamTwo);
    }

    private EmbedBuilder createEmbedServerInfo(String name, int players, String status, String map, int totalQueue, String playTime, String teamOne, String teamTwo) {

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

        return eb;
    }

    private int calcQueue(int pubQueue, int resQueue) {
        return pubQueue + resQueue;
    }

    private String parsePlayTime(int playTime) {
        return String.format("%02d:%02d:%02d", playTime / 3600, (playTime % 3600) / 60, (playTime % 60));
    }

    private String parseTeamName(String teamName){
        ArrayList<String> validTeamNames = new ArrayList<>();
        validTeamNames.add(0, "USA");
        validTeamNames.add(1, "USMC");
        validTeamNames.add(2, "AUS");
        validTeamNames.add(3, "RUS");
        validTeamNames.add(4, "RU");
        validTeamNames.add(5, "GB");
        validTeamNames.add(6, "INS");
        validTeamNames.add(7, "MIL");
        validTeamNames.add(8, "MEA");
        validTeamNames.add(9, "CAF");

        teamName = teamName.toUpperCase();
        for (String factionName : validTeamNames) {
            if (teamName.contains(factionName)) {
                return factionName;
            }
        }
        return teamName;
    }
}
