package serverInfoBot.service;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.api.controller.BattlemetricsController;
import serverInfoBot.api.model.NextLayer;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.db.entities.Factions;
import serverInfoBot.db.entities.LayerInformation;
import serverInfoBot.db.repositories.FactionsRepository;
import serverInfoBot.db.repositories.LayerInformationRepository;


import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Setter
@Getter
@Service
public class BattlemetricsService {

    private BattlemetricsController battlemetricsController;
    private NextLayer nextLayer;
    private ServerInfo serverInfo;
    private FactionsRepository factionsRepository;
    private LayerInformationRepository layerInformationRepository;

    @Autowired
    public BattlemetricsService(BattlemetricsController battlemetricsController, NextLayer nextLayer, ServerInfo serverInfo, FactionsRepository factionsRepository, LayerInformationRepository layerInformationRepository) {
        this.battlemetricsController = battlemetricsController;
        this.nextLayer = nextLayer;
        this.serverInfo = serverInfo;
        this.factionsRepository = factionsRepository;
        this.layerInformationRepository = layerInformationRepository;
    }

    public EmbedBuilder getServerInfo() {

        battlemetricsController.getData();
        String layer = serverInfo.getLayer();
        String nextLayerName = nextLayer.getNextLayer();
        System.out.println(layer);
        System.out.println(nextLayerName);
        LayerInformation layerInformation = getLayerInformationByLayerName(layer);
        LayerInformation nextLayerInformation = getNextLayerInformationByLayerName(nextLayerName);
        String teamOneNext = factionsRepository.findByFactionLong(nextLayerInformation.getTeamOne()).getFactionShort();
        String teamTwoNext = factionsRepository.findByFactionLong(nextLayerInformation.getTeamTwo()).getFactionShort();
        String nextLayerNameAdjusted = nextLayerInformation.getLayerName();
        int totalQueue = calcQueue(serverInfo.getPubQueue(), serverInfo.getResQueue());
        String playTime = parsePlayTime(serverInfo.getPlayTime());
        String teamOne = parseTeamName(serverInfo.getTeamOne());
        String teamTwo = parseTeamName(serverInfo.getTeamTwo());
        String mapImage = layerInformation.getMapImageLink();
        String squadlanes = layerInformation.getSquadlanesLink();
        String squadlanesNext = nextLayerInformation.getSquadlanesLink();

        return createEmbedServerInfo(serverInfo.getName(), serverInfo.getPlayers(), serverInfo.getStatus(), layer, totalQueue, playTime, teamOne, teamTwo, mapImage, nextLayerNameAdjusted, squadlanes, teamOneNext, teamTwoNext, squadlanesNext);
    }

    private EmbedBuilder createEmbedServerInfo(String name, int players, String status, String layer, int totalQueue, String playTime, String teamOne, String teamTwo, String mapImage, String nextLayer, String squadlanes, String teamOneNext, String teamTwoNext, String squadlanesNext) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(name, null);

        eb.setColor(new Color(255, 196, 12));
        eb.addField(":flag_de: Status:", status, true);
        eb.addField(":busts_in_silhouette: Spielerzahl:", players + " +" + totalQueue + " in Queue", true);
        eb.addField(":map: Map:", layer, true);
        //eb.addField(":globe_with_meridians: Zustand:", "leer", true); //TODO Zustand implementieren
        eb.addBlankField(true); //Entfernen, wenn Zustand angezeigt werden soll
        eb.addField(":clock10: Rundenzeit:", playTime, true);
        eb.addField(":flag_white: Fraktionen:", teamOne + " vs " + teamTwo, true);
        eb.addField(":beginner: Squadlanes:", squadlanes, false);
        //eb.addField(":minibus: Fahrzeuge "+ teamOne, "Link", false); //TODO Fahrzeugfotos implementieren
        //eb.addField(":minibus: Fahrzeuge "+ teamTwo, "Link", false);
        eb.addBlankField(false);
        eb.addField("NÄCHSTES \n MATCH", "", true);
        eb.addField(":map: Map:", nextLayer, true);
        eb.addField(":flag_white: Fraktionen:", teamOneNext + " vs " + teamTwoNext, true);
        eb.addField(":beginner: Squadlanes:", squadlanesNext, false);
        //eb.addField(":minibus: Fahrzeuge "+ teamOneNext, "Link", false);
        //eb.addField(":minibus: Fahrzeuge "+ teamTwoNext, "Link", false);
        //eb.addField(":exclamation: Match-Start Benachrichtigung", "Setze die :bell:-Reaktion, wenn du zu Anfang des nächsten Matches gepingt werden möchtest!", false);
        //eb.addBlankField(false);
        //eb.addBlankField(true);
        //eb.addField("ALLGEMEINE INFORMATIONEN", "", true);
        //eb.addBlankField(true);
        //eb.addField("MONTAG-FREITAG", "Im Durchschnitt geht der Server um x Uhr live.\n In der Regel dauert das Seeding x Minuten.\n Für gewöhnlich beginnt das Seeding um x Uhr.\n", false);  //TODO Allgemeine Infos implementieren
        //eb.addField("SAMSTAG-SONNTAG", "Im Durchschnitt geht der Server um x Uhr live.\n In der Regel dauert das Seeding x Minuten.\n Für gewöhnlich beginnt das Seeding um x Uhr.\n", false);
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
        List<Factions> factions = factionsRepository.findAll();
        teamName = teamName.toUpperCase();

        for (Factions faction : factions) {
            String factionShort = faction.getFactionShort();
            if (teamName.contains(factionShort)) {
                return factionShort;
            }
        }
        return teamName;
    }

    private LayerInformation getLayerInformationByLayerName(String layerName){

        return layerInformationRepository.findByLayerName(layerName);
    }

    private LayerInformation getNextLayerInformationByLayerName(String layerName){
        List<LayerInformation> layerInformations = layerInformationRepository.findAll();

        //Workaround for Tallil
        if (layerName.contains("Outskirts")) {
            layerName = layerName.replace(" Outskirts","");
        }

        String adjustedLayerName = layerName.replace("Next level is", "").replace(", layer is", "").trim().replace(" ", "_");
        System.out.println(adjustedLayerName);

        for (LayerInformation layerInformation : layerInformations) {

            String generalLayername = layerInformation.getLayerName();
            String generalMapName = layerInformation.getMapName();
            String adjustedGeneralName = generalMapName + "_" + generalLayername;
            System.out.println(adjustedGeneralName);
            if (adjustedGeneralName.equals(adjustedLayerName)) {
                return layerInformation;
            }
        }
        return null;
    }
}
