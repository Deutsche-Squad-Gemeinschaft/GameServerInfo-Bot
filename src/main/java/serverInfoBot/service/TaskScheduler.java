package serverInfoBot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import serverInfoBot.api.model.NextLayer;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.config.Configuration;
import serverInfoBot.customExceptions.HandledException;
import serverInfoBot.db.entities.FlagTimeInformation;
import serverInfoBot.db.entities.Settings;
import serverInfoBot.db.entities.TimeAverages;
import serverInfoBot.db.repositories.FlagTimeInformationRepository;
import serverInfoBot.db.repositories.SettingsRepository;
import serverInfoBot.db.repositories.TimeAveragesRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class TaskScheduler {

    private final BattlemetricsService battlemetricsService;
    private final Configuration configuration;
    private final NextLayer nextLayer;
    private final ServerInfo serverInfo;
    private final SettingsRepository settingsRepository;
    private final TimeAveragesRepository timeAveragesRepository;
    private final FlagTimeInformationRepository flagTimeInformationRepository;
    private final String TASK_SCHEDULER_EXCEPTION = "TASK_SCHEDULER_EXCEPTION";

    //TODO Use Spring Scheduler

    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    public void startScheduleTask(JDA jda) {


        List<ItemComponent> list = new ArrayList<>();
        list.add(Button.secondary("Match-Start Benachrichtigung", "Match-Start Benachrichtigung").withEmoji(Emoji.fromUnicode("U+1F514")));

        final ScheduledFuture<?> taskHandle = scheduler.scheduleWithFixedDelay(
                () -> {
                    try {

                        Settings settings = settingsRepository.findById(1);
                        EmbedBuilder eb = battlemetricsService.getServerInfo();

                        if (configuration.isProd == 1) {

                            if(configuration.getSendNewMessages() == 1){
                                jda.getTextChannelById(settings.getJgkpTextChannelId()).deleteMessageById(settings.getJgkpMessageId()).queue();
                                jda.getTextChannelById(settings.getJgkpTextChannelId()).sendMessageEmbeds(eb.build()).addActionRow(Button.primary("notification", "Match-Start Benachrichtigung")).queue((message) -> settings.setJgkpMessageId(message.getId()));

                                jda.getTextChannelById(settings.getDsgTextChannelId()).deleteMessageById(settings.getDsgMessageId()).queue();
                                jda.getTextChannelById(settings.getDsgTextChannelId()).sendMessageEmbeds(eb.build()).addActionRow(Button.primary("notification", "Match-Start Benachrichtigung")).queue((message) -> {
                                    settings.setDsgMessageId(message.getId());
                                    settingsRepository.save(settings);
                                });
                                System.out.println("Replaced Messages (prod)");
                            }

                            //jgkp-server
                            Objects.requireNonNull(jda.getTextChannelById(settings.getJgkpTextChannelId())).retrieveMessageById(settings.getJgkpMessageId()).queue(message -> message.editMessageEmbeds(eb.build()).setActionRow(list).queue());

                            //dsg-server
                            Objects.requireNonNull(jda.getTextChannelById(settings.getDsgTextChannelId())).retrieveMessageById(settings.getDsgMessageId()).queue(message -> message.editMessageEmbeds(eb.build()).setActionRow(list).queue());

                            System.out.println("Updated Embed (prod)");

                        } else if (configuration.isProd == 0) {

                            if(configuration.getSendNewMessages() == 1){
                                jda.getTextChannelById(settings.getTestTextChannelId()).deleteMessageById(settings.getTestMessageId()).queue();
                                jda.getTextChannelById(settings.getTestTextChannelId()).sendMessageEmbeds(eb.build()).addActionRow(Button.primary("Test", "Match-Start Benachrichtigung")).queue((message) -> {
                                    settings.setTestMessageId(message.getId());
                                    settingsRepository.save(settings);
                                });
                                System.out.println("Replaced Messages (localdev)");
                            }

                            //test-server
                            Objects.requireNonNull(jda.getTextChannelById(settings.getTestTextChannelId())).retrieveMessageById(settings.getTestMessageId()).queue(message -> message.editMessageEmbeds(eb.build()).setActionRow(list).queue());

                            System.out.println("Updated Embed (localdev)");
                        }else {
                            throw new HandledException(TASK_SCHEDULER_EXCEPTION, "Failed to select Embed Destinations");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, 1, TimeUnit.MINUTES);
    }

    public void startScheduleTaskNextLayer(){

        final ScheduledFuture<?> taskHandle = scheduler.scheduleWithFixedDelay(
                () -> {

                    if (serverInfo.getPlayers() != 0 || !nextLayer.isExecutedFirstTime()) {
                        try {
                            // Construct the URL to send the RCON command
                            String rconUrl = "https://api.battlemetrics.com/servers/3219649/command";

                            // Set up the HTTP connection
                            URL url = new URL(rconUrl);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Authorization", configuration.getBattlemetricsShowNextMapAPIToken());
                            con.setRequestProperty("Content-Type", "application/json");
                            con.setDoOutput(true);

                            // Construct the JSON payload for the RCON command
                            String jsonPayload = "{\"data\":{\"type\":\"rconCommand\",\"attributes\":{\"command\":\"squad:showNextMap\"}}}";

                            // Send the JSON payload as the HTTP request body
                            con.getOutputStream().write(jsonPayload.getBytes(StandardCharsets.UTF_8));

                            // Read the response from the server
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String responseNextLayer = in.readLine();

                            in.close();

                            // Convert the response to a JSON object
                            JSONObject jsonResponse = new JSONObject(responseNextLayer);

                            String nextLayerVar = jsonResponse.getJSONObject("data").getJSONObject("attributes").getString("result");
                            nextLayer.setNextLayer(nextLayerVar);
                            nextLayer.setExecutedFirstTime(true);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 5, TimeUnit.MINUTES);
    }

    public void updateFlagTimeInformationInPanel(){

        final ScheduledFuture<?> taskHandle = scheduler.scheduleWithFixedDelay(
                () -> {

                        try {
                            //Calculate Mo-Fr
                            List<FlagTimeInformation> workdays = new ArrayList<>();
                            workdays.addAll(flagTimeInformationRepository.findByWeekday("Mon"));
                            workdays.addAll(flagTimeInformationRepository.findByWeekday("Tue"));
                            workdays.addAll(flagTimeInformationRepository.findByWeekday("Wed"));
                            workdays.addAll(flagTimeInformationRepository.findByWeekday("Thur"));
                            workdays.addAll(flagTimeInformationRepository.findByWeekday("Fri"));

                            List<List<String>> workdaysInformation = gatherData(workdays);

                            String averageWorkdayLiveTime = calculateAverage(convertTimeToMinutes(workdaysInformation.get(0)));
                            String averageWorkdaySeedingDuration = calculateAverage(convertTimeToMinutes(workdaysInformation.get(1)));
                            String averageWorkdaySeedingStartTime = calculateAverage(convertTimeToMinutes(workdaysInformation.get(2)));

                            TimeAverages timeAverages = timeAveragesRepository.findById(1).orElse(null);
                            timeAverages.setAverageLiveTimeWorkday(averageWorkdayLiveTime);
                            timeAverages.setAverageSeedingDurationWorkday(averageWorkdaySeedingDuration);
                            timeAverages.setAverageSeedingStartTimeWorkday(averageWorkdaySeedingStartTime);

                            //Calculate Sa-So
                            List<FlagTimeInformation> weekend = new ArrayList<>();
                            weekend.addAll(flagTimeInformationRepository.findByWeekday("Sat"));
                            weekend.addAll(flagTimeInformationRepository.findByWeekday("Sun"));

                            List<List<String>> weekendInformation = gatherData(weekend);

                            String averageWeekendLiveTime = calculateAverage(convertTimeToMinutes(weekendInformation.get(0)));
                            String averageWeekendSeedingDuration = calculateAverage(convertTimeToMinutes(weekendInformation.get(1)));
                            String averageWeekendSeedingStartTime = calculateAverage(convertTimeToMinutes(weekendInformation.get(2)));

                            timeAverages.setAverageLiveTimeWeekend(averageWeekendLiveTime);
                            timeAverages.setAverageSeedingDurationWeekend(averageWeekendSeedingDuration);
                            timeAverages.setAverageSeedingStartTimeWeekend(averageWeekendSeedingStartTime);

                            timeAveragesRepository.save(timeAverages);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                }, 0, 1, TimeUnit.DAYS);
    }

    private List<List<String>> gatherData(List<FlagTimeInformation> flagTimeList){

        List<List<String>> flagTimeInformations = new ArrayList<>();
        List<String> liveTimes = new ArrayList<>();
        List<String> seedingDurations = new ArrayList<>();
        List<String> seedingStartTimes = new ArrayList<>();

        for (FlagTimeInformation flagTimeInformation : flagTimeList) {
            seedingStartTimes.add(flagTimeInformation.getSeedingStartTime());
            liveTimes.add(flagTimeInformation.getLiveTime());
            seedingDurations.add(flagTimeInformation.getSeedingDuration());
        }

        flagTimeInformations.add(liveTimes);
        flagTimeInformations.add(seedingDurations);
        flagTimeInformations.add(seedingStartTimes);

        return flagTimeInformations;
    }

    private List<Integer> convertTimeToMinutes(List<String> workdayLiveTimes){

        List<Integer> workdayLiveTimesMinutes = new ArrayList<>();
        for (String workdayLiveTime : workdayLiveTimes) {
            if (workdayLiveTime != null) {
                int hours = Integer.parseInt(workdayLiveTime.substring(0, 2));
                int minutes = Integer.parseInt(workdayLiveTime.substring(3, 5));
                int totalMinutes = hours * 60 + minutes;
                workdayLiveTimesMinutes.add(totalMinutes);
            }
        }
        return workdayLiveTimesMinutes;
    }

    private String calculateAverage(List<Integer> workdayLiveTimesMinutes){
        int workdayLiveTimesMinutesAverage = (int) workdayLiveTimesMinutes.stream()
                .mapToInt(a -> a)
                .average().orElse(0);

        int averageHours =  workdayLiveTimesMinutesAverage / 60;

        String averageHoursString = String.valueOf(averageHours);
        if (averageHours < 10){
            averageHoursString = "0" + averageHours;
        }

        int averageMinutes = workdayLiveTimesMinutesAverage % 60;

        String averageMinutesString = String.valueOf(averageMinutes);
        if (averageMinutes < 10){
            averageMinutesString = "0" + averageMinutes;
        }

        return averageHoursString + ":" + averageMinutesString;
    }
}
