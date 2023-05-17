package serverInfoBot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.springframework.stereotype.Service;
import serverInfoBot.api.controller.BattlemetricsController;
import serverInfoBot.api.model.NextLayer;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.db.entities.Factions;
import serverInfoBot.db.entities.LastRequest;
import serverInfoBot.db.entities.LayerInformation;
import serverInfoBot.db.entities.Settings;
import serverInfoBot.db.repositories.FactionsRepository;
import serverInfoBot.db.repositories.LastRequestRepository;
import serverInfoBot.db.repositories.LayerInformationRepository;
import serverInfoBot.db.repositories.SettingsRepository;
import serverInfoBot.discord.Bot;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class BattlemetricsService {

    private final BattlemetricsController battlemetricsController;
    private final NextLayer nextLayer;
    private final ServerInfo serverInfo;
    private final FactionsRepository factionsRepository;
    private final LayerInformationRepository layerInformationRepository;
    private final LastRequestRepository lastRequestRepository;
    private final Bot bot;
    private final SettingsRepository settingsRepository;

    public EmbedBuilder getServerInfo() {

        battlemetricsController.getData();
        String layer = serverInfo.getLayer();
        String nextLayerName = nextLayer.getNextLayer();
        System.out.println(layer);
        System.out.println(nextLayerName);
        LayerInformation layerInformation = getLayerInformationByLayerName(layer);
        LayerInformation nextLayerInformation = getNextLayerInformationByNextLayerName(nextLayerName);
        String teamOneNext = factionsRepository.findByFactionLong(nextLayerInformation.getTeamOne()).getFactionShort();
        String teamTwoNext = factionsRepository.findByFactionLong(nextLayerInformation.getTeamTwo()).getFactionShort();
        String nextLayerNameAdjusted = nextLayerInformation.getCurrentLayerName();
        int totalQueue = calcQueue(serverInfo.getPubQueue(), serverInfo.getResQueue());
        String playTime = parsePlayTime(serverInfo.getPlayTime());
        String teamOne = parseTeamName(serverInfo.getTeamOne());
        String teamTwo = parseTeamName(serverInfo.getTeamTwo());
        String mapImage = layerInformation.getMapImageLink();
        String squadlanes = layerInformation.getSquadlanesLink();
        String squadlanesNext = nextLayerInformation.getSquadlanesLink();
        int players = serverInfo.getPlayers();
        String status = serverInfo.getStatus();
        String name = serverInfo.getName();

        LastRequest lastRequest = lastRequestRepository.findById(1);

        checkForMapchange(lastRequest.getLayer(), layer);

        lastRequest.setPlayers(players);
        lastRequest.setLayer(layer);
        lastRequest.setStatus(status);
        lastRequest.setTeamOne(teamOne);
        lastRequest.setTeamTwo(teamTwo);
        lastRequest.setServerName(name);
        lastRequest.setPlaytime(playTime);
        lastRequest.setTotalQueue(totalQueue);
        lastRequestRepository.save(lastRequest);



        return createEmbedServerInfo(name, players, status, layer, totalQueue, playTime, teamOne, teamTwo, mapImage, nextLayerNameAdjusted, squadlanes, teamOneNext, teamTwoNext, squadlanesNext);
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
        eb.addField(":exclamation: Match-Start Benachrichtigung", "Klicke auf den Button unter der Nachricht, wenn du zu Anfang des nächsten Matches gepingt werden möchtest!", false);
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

    private String parseTeamName(String teamName) {
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

    private LayerInformation getLayerInformationByLayerName(String layerName) {

        return layerInformationRepository.findByCurrentLayerName(layerName);
    }

    private LayerInformation getNextLayerInformationByNextLayerName(String nextLayerName) {

        String adjustedLayerName = nextLayerName.replace("Next level is", "").replace(", layer is", "").trim().replace(" ", "_");
        System.out.println(adjustedLayerName);

        LayerInformation layerInformation = layerInformationRepository.findByNextLayerName(adjustedLayerName);

        if (layerInformation.getNextLayerName().equals(adjustedLayerName)) {
            return layerInformation;
        }
        return null;
    }

    private void checkForMapchange(String oldLayer, String newLayer){
        if (!oldLayer.equals(newLayer)) {
            Settings settings = settingsRepository.findById(1);
            JDA jda = bot.getJda();

            List<Member> member = jda.getGuildById(settings.getTestGuildId()).findMembersWithRoles(jda.getGuildById(settings.getTestGuildId()).getRolesByName("Match-Start Notification", false).get(0)).get();

            if (member.size() != 0){
                jda.getTextChannelById(settings.getTestTextChannelId()).sendMessage("**Match-Start Benachrichtigung** \nDas nächste Match hat begonnen! <@&"+jda.getGuildById(settings.getTestGuildId()).getRolesByName("Match-Start Notification", false).get(0).getId()+">").delay(Duration.ofMinutes(10)).flatMap(Message::delete).queue();

                for (Member value : member) {
                    jda.getGuildById(settings.getTestGuildId()).removeRoleFromMember(value, jda.getGuildById(settings.getTestGuildId()).getRolesByName("Match-Start Notification", false).get(0)).queue();
                }
            }

            List<Member> memberDsg = jda.getGuildById(settings.getDsgGuildId()).findMembersWithRoles(jda.getGuildById(settings.getDsgGuildId()).getRolesByName("Match-Start Notification", false).get(0)).get();

            if (memberDsg.size() != 0){
                jda.getTextChannelById(settings.getDsgTextChannelId()).sendMessage("**Match-Start Benachrichtigung** \nDas nächste Match hat begonnen! <@&"+jda.getGuildById(settings.getDsgGuildId()).getRolesByName("Match-Start Notification", false).get(0).getId()+">").delay(Duration.ofMinutes(10)).flatMap(Message::delete).queue();

                for (Member value : memberDsg) {
                    jda.getGuildById(settings.getDsgGuildId()).removeRoleFromMember(value, jda.getGuildById(settings.getDsgGuildId()).getRolesByName("Match-Start Notification", false).get(0)).queue();
                }
            }

            List<Member> memberjgkp = jda.getGuildById(settings.getJgkpGuildId()).findMembersWithRoles(jda.getGuildById(settings.getJgkpGuildId()).getRolesByName("Match-Start Notification", false).get(0)).get();

            if (memberjgkp.size() != 0){
                jda.getTextChannelById(settings.getJgkpTextChannelId()).sendMessage("**Match-Start Benachrichtigung** \nDas nächste Match hat begonnen! <@&"+jda.getGuildById(settings.getJgkpGuildId()).getRolesByName("Match-Start Notification", false).get(0).getId()+">").delay(Duration.ofMinutes(10)).flatMap(Message::delete).queue();

                for (Member value : memberjgkp) {
                    jda.getGuildById(settings.getJgkpGuildId()).removeRoleFromMember(value, jda.getGuildById(settings.getJgkpGuildId()).getRolesByName("Match-Start Notification", false).get(0)).queue();
                }
            }
        }
    }
}
