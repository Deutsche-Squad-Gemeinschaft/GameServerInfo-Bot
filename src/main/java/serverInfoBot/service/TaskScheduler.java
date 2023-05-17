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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.api.model.NextLayer;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.config.Configuration;
import serverInfoBot.customExceptions.HandledException;
import serverInfoBot.db.entities.Settings;
import serverInfoBot.db.repositories.SettingsRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                                jda.getTextChannelById(settings.getJgkpTextChannelId()).sendMessageEmbeds(eb.build()).addActionRow(Button.primary("notification", "Match-Start Benachrichtigung")).queue((message) -> {
                                    settings.setJgkpMessageId(message.getId());
                                });

                                jda.getTextChannelById(settings.getDsgTextChannelId()).deleteMessageById(settings.getDsgMessageId()).queue();
                                jda.getTextChannelById(settings.getDsgTextChannelId()).sendMessageEmbeds(eb.build()).addActionRow(Button.primary("notification", "Match-Start Benachrichtigung")).queue((message) -> {
                                    settings.setDsgMessageId(message.getId());
                                    settingsRepository.save(settings);
                                });
                                System.out.println("Replaced Messages (prod)");
                            }

                            //jgkp-server
                            Objects.requireNonNull(jda.getTextChannelById(settings.getJgkpTextChannelId())).retrieveMessageById(settings.getJgkpMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).setActionRow(list).queue();
                            });

                            //dsg-server
                            Objects.requireNonNull(jda.getTextChannelById(settings.getDsgTextChannelId())).retrieveMessageById(settings.getDsgMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).setActionRow(list).queue();
                            });

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
                            Objects.requireNonNull(jda.getTextChannelById(settings.getTestTextChannelId())).retrieveMessageById(settings.getTestMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).setActionRow(list).queue();
                            });

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
                            con.getOutputStream().write(jsonPayload.getBytes("UTF-8"));

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
//TODO calculate the new averages for the info panel once a day.
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                }, 0, 1, TimeUnit.DAYS);
    }
}
