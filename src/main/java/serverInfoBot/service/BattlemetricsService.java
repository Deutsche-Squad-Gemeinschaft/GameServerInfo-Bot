package serverInfoBot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import serverInfoBot.api.controller.BattlemetricsController;
import serverInfoBot.api.model.NextLayer;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.config.Configuration;
import serverInfoBot.db.entities.*;
import serverInfoBot.db.repositories.*;
import serverInfoBot.discord.Bot;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Service
@RequiredArgsConstructor
public class BattlemetricsService {

    private final BattlemetricsController battlemetricsController;
    private final Configuration configuration;
    private final NextLayer nextLayer;
    private final ServerInfo serverInfo;
    private final FactionsRepository factionsRepository;
    private final LayerInformationRepository layerInformationRepository;
    private final LastRequestRepository lastRequestRepository;
    private final Bot bot;
    private final SettingsRepository settingsRepository;
    private final FlagTimeInformationRepository flagTimeInformationRepository;
    private final MatchHistoryRepository matchHistoryRepository;
    private final LastLoggedMatchRepository lastLoggedMatchRepository;
    private final TimeAveragesRepository timeAveragesRepository;
    public EmbedBuilder getServerInfo() {

        battlemetricsController.getData();
        String layer = serverInfo.getLayer();
        String nextLayerName = nextLayer.getNextLayer();
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
        String squadmaps = layerInformation.getSquadmapsLink();
        String squadmapsNext = nextLayerInformation.getSquadmapsLink();
        String mapName = layerInformation.getMapName();

        LastRequest lastRequest = lastRequestRepository.findById(1);

        boolean mapchange = checkForMapchange(lastRequest.getLayer(), layer);

        String flag = checkForFlag(players, layer);

        DateTime dt = new DateTime();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        dt = dt.withZone(timeZone);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("dd.MM.yyyy");
        DateTimeFormatter fmt3 = DateTimeFormat.forPattern("E");
        DateTimeFormatter fmt4 = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
        String date = fmt2.print(dt);
        String time = fmt.print(dt);
        String weekday = fmt3.print(dt);
        String dateTime = fmt4.print(dt);

        if (mapchange) {
            logMatches(layer, flag, date, time, dateTime, mapName);
        }

        checkForFlagChange(lastRequest.getFlag(), flag, date, time, weekday);

        checkForSeedingCancellation(players, date);

        lastRequest.setDate(date);
        lastRequest.setTime(time);
        lastRequest.setFlag(flag);
        lastRequest.setPlayers(players);
        lastRequest.setLayer(layer);
        lastRequest.setStatus(status);
        lastRequest.setTeamOne(teamOne);
        lastRequest.setTeamTwo(teamTwo);
        lastRequest.setServerName(name);
        lastRequest.setPlaytime(playTime);
        lastRequest.setTotalQueue(totalQueue);
        lastRequestRepository.save(lastRequest);

        TimeAverages timeAverages = timeAveragesRepository.findById(1).orElse(null);
        String workdayLiveTime = timeAverages.getAverageLiveTimeWorkday();
        String workdaySeedingDuration = timeAverages.getAverageSeedingDurationWorkday();
        String workdaySeedingStartTime = timeAverages.getAverageSeedingStartTimeWorkday();
        String weekendLiveTime = timeAverages.getAverageLiveTimeWeekend();
        String weekendSeedingDuration = timeAverages.getAverageSeedingDurationWeekend();
        String weekendSeedingStartTime = timeAverages.getAverageSeedingStartTimeWeekend();

        return createEmbedServerInfo(name, players, status, layer, totalQueue, playTime, teamOne, teamTwo, mapImage, nextLayerNameAdjusted, squadlanes, teamOneNext, teamTwoNext, squadlanesNext, squadmaps, squadmapsNext, workdayLiveTime, workdaySeedingDuration, workdaySeedingStartTime, weekendLiveTime, weekendSeedingDuration, weekendSeedingStartTime);
    }

    private EmbedBuilder createEmbedServerInfo(String name, int players, String status, String layer, int totalQueue, String playTime, String teamOne, String teamTwo, String mapImage, String nextLayer, String squadlanes, String teamOneNext, String teamTwoNext, String squadlanesNext, String squadmaps, String squadmapsNext, String workdayLiveTime, String workdaySeedingDuration, String workdaySeedingStartTime, String weekendLiveTime, String weekendSeedingDuration, String weekendSeedingStartTime) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(name, null);

        eb.setColor(new Color(255, 196, 12));
        eb.addField(":flag_de: Status:", status, true);
        eb.addField(":busts_in_silhouette: Spielerzahl:", players + " +" + totalQueue + " in Queue", true);
        eb.addField(":map: Map:", layer, true);
        eb.addBlankField(true);
        eb.addField(":clock10: Rundenzeit:", playTime, true);
        eb.addField(":flag_white: Fraktionen:", teamOne + " vs " + teamTwo, true);
        eb.addField(":beginner: Squadlanes:", squadlanes, false);
        eb.addField(":pushpin: Squadmaps: ", squadmaps, false);
        eb.addBlankField(false);
        eb.addField("NÄCHSTES \nMATCH", "", true);
        eb.addField(":map: Map:", nextLayer, true);
        eb.addField(":flag_white: Fraktionen:", teamOneNext + " vs " + teamTwoNext, true);
        eb.addField(":beginner: Squadlanes:", squadlanesNext, false);
        eb.addField(":pushpin: Squadmaps: ", squadmapsNext, false);
        eb.addField(":exclamation: Match-Start Benachrichtigung", "Klicke auf den Button unter der Nachricht, wenn du zu Anfang des nächsten Matches gepingt werden möchtest!", false);

        eb.addBlankField(false);

        eb.addField("WERKTAGS", "Seedingstart: **"+ workdaySeedingStartTime +" Uhr**\n" +
                                                    "Seedingende: **"+ workdayLiveTime +" Uhr**\n " +
                                                    "Seedinglänge: **"+ workdaySeedingDuration +"h**\n ", true);
        eb.addField("WOCHENENDE", "Seedingstart: **"+ weekendSeedingStartTime +" Uhr**\n" +
                                                   "Seedingende: **"+ weekendLiveTime +" Uhr**\n " +
                                                    "Seedinglänge: **"+ weekendSeedingDuration +"h**\n ", true);

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

        LayerInformation layerInformation = layerInformationRepository.findByNextLayerName(adjustedLayerName);

        if (layerInformation.getNextLayerName().equals(adjustedLayerName)) {
            return layerInformation;
        }
        return null;
    }

    private boolean checkForMapchange(String oldLayer, String newLayer) {

        if (!oldLayer.equals(newLayer)) {
            Settings settings = settingsRepository.findById(1);
            JDA jda = bot.getJda();

            if (configuration.isProd == 0) {
                List<Member> member = jda.getGuildById(settings.getTestGuildId()).findMembersWithRoles(jda.getGuildById(settings.getTestGuildId()).getRolesByName("Match-Start Notification", false).get(0)).get();

                if (member.size() != 0) {
                    jda.getTextChannelById(settings.getTestTextChannelId()).sendMessage("**Match-Start Benachrichtigung** \nDas nächste Match hat begonnen! <@&" + jda.getGuildById(settings.getTestGuildId()).getRolesByName("Match-Start Notification", false).get(0).getId() + ">").delay(Duration.ofMinutes(10)).flatMap(Message::delete).queue();

                    for (Member value : member) {
                        jda.getGuildById(settings.getTestGuildId()).removeRoleFromMember(value, jda.getGuildById(settings.getTestGuildId()).getRolesByName("Match-Start Notification", false).get(0)).queue();
                    }
                }
            }

            if (configuration.isProd == 1) {
                List<Member> memberDsg = jda.getGuildById(settings.getDsgGuildId()).findMembersWithRoles(jda.getGuildById(settings.getDsgGuildId()).getRolesByName("Match-Start Notification", false).get(0)).get();

                if (memberDsg.size() != 0) {
                    jda.getTextChannelById(settings.getDsgTextChannelId()).sendMessage("**Match-Start Benachrichtigung** \nDas nächste Match hat begonnen! <@&" + jda.getGuildById(settings.getDsgGuildId()).getRolesByName("Match-Start Notification", false).get(0).getId() + ">").delay(Duration.ofMinutes(10)).flatMap(Message::delete).queue();

                    for (Member value : memberDsg) {
                        jda.getGuildById(settings.getDsgGuildId()).removeRoleFromMember(value, jda.getGuildById(settings.getDsgGuildId()).getRolesByName("Match-Start Notification", false).get(0)).queue();
                    }
                }

                List<Member> memberjgkp = jda.getGuildById(settings.getJgkpGuildId()).findMembersWithRoles(jda.getGuildById(settings.getJgkpGuildId()).getRolesByName("Match-Start Notification", false).get(0)).get();

                if (memberjgkp.size() != 0) {
                    jda.getTextChannelById(settings.getJgkpTextChannelId()).sendMessage("**Match-Start Benachrichtigung** \nDas nächste Match hat begonnen! <@&" + jda.getGuildById(settings.getJgkpGuildId()).getRolesByName("Match-Start Notification", false).get(0).getId() + ">").delay(Duration.ofMinutes(10)).flatMap(Message::delete).queue();

                    for (Member value : memberjgkp) {
                        jda.getGuildById(settings.getJgkpGuildId()).removeRoleFromMember(value, jda.getGuildById(settings.getJgkpGuildId()).getRolesByName("Match-Start Notification", false).get(0)).queue();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private String checkForFlag(int players, String layer) {

        if (players <= 2) {
            return "Leer";
        }
        if (players <= 50 && (layer.contains("Skirmish") || layer.contains("Seed"))) {
            return "Seeding";
        }
        if (players > 50) {
            return "Live";
        }
        if (players <= 50 && (!layer.contains("Skirmish") || !layer.contains("Seed"))) {
            return "Dead";
        }
        return "Unknown";
    }

    private void checkForFlagChange(String oldFlag, String newFlag, String date, String time, String weekday) {

        FlagTimeInformation flagTimeInformation = flagTimeInformationRepository.findByDate(date);

        if (flagTimeInformation == null) {
            FlagTimeInformation newFlagTimeInformation = new FlagTimeInformation();
            newFlagTimeInformation.setDate(date);
            newFlagTimeInformation.setWeekday(weekday);
            flagTimeInformationRepository.save(newFlagTimeInformation);
        }

        flagTimeInformation = flagTimeInformationRepository.findByDate(date);

        if (oldFlag.equals("Leer") && newFlag.equals("Seeding") && flagTimeInformation.getSeedingStartTime() == null){
            flagTimeInformation.setSeedingStartTime(time);

            sendSeedingStartAnnouncement();
        }

        if ((oldFlag.equals("Seeding") || oldFlag.equals("Dead")) && newFlag.equals("Live") && flagTimeInformation.getLiveTime() == null) {
            flagTimeInformation.setLiveTime(time);

            sendSeedingEndAnnouncement();

            String seedingStartTime = flagTimeInformation.getSeedingStartTime();

            SimpleDateFormat dfGerman = new SimpleDateFormat("HH:mm");

            try {
                Date startDate = dfGerman.parse(seedingStartTime);
                Date endDate = dfGerman.parse(time);
                Date resultDate = new Date(endDate.getTime() - startDate.getTime() + dfGerman.parse("00:00").getTime());

                flagTimeInformation.setSeedingDuration(dfGerman.format(resultDate));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        flagTimeInformationRepository.save(flagTimeInformation);
    }

    private void checkForSeedingCancellation(int players, String date){

        if(players < 3){
            FlagTimeInformation flagTimeInformation = flagTimeInformationRepository.findByDate(date);
            if(flagTimeInformation.getSeedingStartTime()!= null && flagTimeInformation.getLiveTime() == null){
                flagTimeInformation.setSeedingStartTime(null);
                flagTimeInformationRepository.save(flagTimeInformation);
            }
        }
    }

    private void logMatches(String layer, String flag, String date, String time, String dateTime, String mapName){

        MatchHistory matchHistory = new MatchHistory();
        matchHistory.setLayerName(layer);
        matchHistory.setFlag(flag);
        matchHistory.setStartDate(date);
        matchHistory.setStartTime(time);
        matchHistory.setDateTime(dateTime);
        matchHistory.setMapName(mapName);
        matchHistoryRepository.save(matchHistory);

        MatchHistory matchBefore = matchHistoryRepository.findByDateTime(lastLoggedMatchRepository.findById(1).get().getDateTime());

        LastLoggedMatch lastLoggedMatch = lastLoggedMatchRepository.findById(1).orElse(null);
        lastLoggedMatch.setDateTime(dateTime);
        lastLoggedMatch.setLayerName(layer);
        lastLoggedMatchRepository.save(lastLoggedMatch);

        if (matchBefore != null){
            matchBefore.setEndDate(date);
            matchBefore.setEndTime(time);

            if (flag.equals("Dead") && matchBefore.getFlag().equals("Leer")){
                matchBefore.setFlag("Seeding");
            }

            if ((matchBefore.getFlag().equals("Leer") || matchBefore.getFlag().equals("Dead") || matchBefore.getFlag().equals("Walking Dead")) && flag.equals("Live")){
                if (matchBefore.getLayerName().contains("Seed") || matchBefore.getLayerName().contains("Skirmish")){
                    matchBefore.setFlag("Seeding");
                } else {
                    matchBefore.setFlag("Live");
                }
            }

            if (flag.equals("Leer") && matchBefore.getFlag().equals("Live")){
                matchBefore.setFlag("Dead");
            }

            if(flag.equals("Dead") && matchBefore.getFlag().equals("Dead")){
                MatchHistory matchHistory1 = matchHistoryRepository.findByDateTime(dateTime);
                matchHistory1.setFlag("Walking Dead");
                matchHistoryRepository.save(matchHistory1);
            }

            SimpleDateFormat dfGerman = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            try {

                Date startDate = dfGerman.parse(matchBefore.getStartDate() + " " +matchBefore.getStartTime());
                Date endDate = dfGerman.parse(date + " " + time);
                Date resultDate = new Date(endDate.getTime() - startDate.getTime() + dfGerman.parse("01.01.0000 00:00").getTime());

                matchBefore.setDuration(dfGerman.format(resultDate).substring(11,16));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            matchHistoryRepository.save(matchBefore);
        }
    }

    public void sendSeedingStartAnnouncement(){
        Settings settings = settingsRepository.findById(1);

        if (configuration.isProd == 1) {
        bot.getJda().getGuildById(settings.getDsgGuildId()).getNewsChannelById(settings.getDsgSeedingAnnouncementChannelId()).sendMessage("<@&"+settings.getDsgServerSeederRoleId()+">").addEmbeds(createEmbedServerSeeding().build()).queue();

        } else if (configuration.isProd == 0) {
            bot.getJda().getGuildById(settings.getTestGuildId()).getNewsChannelById(settings.getTestSeedingAnnouncementChannelId()).sendMessage("<@&"+settings.getTestServerSeederRoleId()+">").addEmbeds(createEmbedServerSeeding().build()).queue();
        }
    }

    public void sendSeedingEndAnnouncement(){
        Settings settings = settingsRepository.findById(1);

        if (configuration.isProd == 1) {
            bot.getJda().getGuildById(settings.getDsgGuildId()).getNewsChannelById(settings.getDsgSeedingAnnouncementChannelId()).sendMessageEmbeds(createEmbedServerSeedingEnd().build()).queue();

        } else if (configuration.isProd == 0) {
            bot.getJda().getGuildById(settings.getTestGuildId()).getNewsChannelById(settings.getTestSeedingAnnouncementChannelId()).sendMessageEmbeds(createEmbedServerSeedingEnd().build()).queue();
        }
    }

    public EmbedBuilder createEmbedServerSeeding() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Das Seeding hat begonnen!" , null);

        eb.setDescription(getRandomSeedingAnnouncementText());

        eb.setColor(new Color(255, 196, 12));
        eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
        eb.setImage(getRandomSeedingAnnouncementImageUrl());
        eb.setTimestamp(Instant.now());

        return eb;
    }

    public EmbedBuilder createEmbedServerSeedingEnd() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Wir sind live!" , null);

        eb.setDescription("Danke für's Seeden <3");

        eb.setColor(new Color(255, 196, 12));
        eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
        eb.setTimestamp(Instant.now());

        return eb;
    }


    public String getRandomSeedingAnnouncementText(){
        List<String> seedingAnnouncementList = new ArrayList<>();
        seedingAnnouncementList.add("Das seeden hat begonnen, kommt ran!");
        seedingAnnouncementList.add("Wir sind am Seeden! Kommt und bringt den Server mit uns zusammen zum Laufen.");
        seedingAnnouncementList.add("Die Heinzelmännchen haben euch schon eine HUB errichtet und alles steht bereit. Es wird angeseedet. Kommt vorbei und spielt mit uns!");
        seedingAnnouncementList.add("Seedingbären\n" +
                "Seeden hier und dort und überall\n" +
                "Sie sind für dich da wenn du sie brauchst\n" +
                "Das sind die Seedingbären!\n" +
                "\n" +
                "Kommense ran!");
        seedingAnnouncementList.add("Der Server füllt sich immer schneller! :slight_smile: Kommense ran!");
        seedingAnnouncementList.add("Wir starten das Seeden,\n" +
                "kommt ran, verdient euch Seedingpunkte\n" +
                "und unterstützt den Server.");
        seedingAnnouncementList.add("Der DSG Server wartet auf seine Spieler! An diesem wunderschönen Tag wird geseedet. Kommt und helft mit!");

        int min = 1;
        int max = seedingAnnouncementList.size();
        int range = max - min + 1;
        int rand = (int) (Math.random() * range) + min;
        return seedingAnnouncementList.get(rand - 1);
    }


    public String getRandomSeedingAnnouncementImageUrl(){
        List<String> seedingAnnouncementImageList = new ArrayList<>();

        seedingAnnouncementImageList.add("https://i.imgur.com/IQqtnM9.jpeg");
        seedingAnnouncementImageList.add("https://i.imgur.com/NS4UjRR.jpeg");
        seedingAnnouncementImageList.add("https://i.imgur.com/v881yAH.png");
        seedingAnnouncementImageList.add("https://i.imgur.com/NxDtXod.jpeg");
        seedingAnnouncementImageList.add("https://i.imgur.com/NboPZVe.png");
        seedingAnnouncementImageList.add("https://i.imgur.com/RnyV4v9.png");
        seedingAnnouncementImageList.add("https://i.imgur.com/w5GcE64.jpeg");
        seedingAnnouncementImageList.add("https://i.imgur.com/Ngq1MBY.png");
        seedingAnnouncementImageList.add("https://i.imgur.com/zqovhnu.png");

        int min = 1;
        int max = seedingAnnouncementImageList.size();
        int range = max - min + 1;
        int rand = (int) (Math.random() * range) + min;
        return seedingAnnouncementImageList.get(rand - 1);
    }
}
