package serverInfoBot.service;

import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.api.BattlemetricsController;
import serverInfoBot.api.model.ServerInfo;

import java.awt.*;

@Service
public class BattlemetricsService {

    private BattlemetricsController battlemetricsController;

    @Autowired
    public BattlemetricsService(BattlemetricsController battlemetricsController) {
        this.battlemetricsController = battlemetricsController;
    }

    public EmbedBuilder getServerInfo(){
        ServerInfo serverInfo = battlemetricsController.getData();

        return createEmbedServerInfo(serverInfo.getName(), serverInfo.getPlayers(), serverInfo.getStatus(), serverInfo.getMap());
    }

    private EmbedBuilder createEmbedServerInfo(String name, int players, String status, String map) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(name, null);

        eb.setColor(new Color(255, 196, 12));

        eb.addField(":flag_de: Status:", status, true);
        eb.addField(":busts_in_silhouette: Spielerzahl:", String.valueOf(players), true);
        eb.addField(":map: Map:", map, true);


        eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");

        return eb;
    }
}
