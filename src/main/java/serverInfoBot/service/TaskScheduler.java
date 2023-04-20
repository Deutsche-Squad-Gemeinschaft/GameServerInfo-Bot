package serverInfoBot.service;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.api.model.NextLayer;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.config.Configuration;
import serverInfoBot.customExceptions.HandledException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Getter
@Setter
public class TaskScheduler {

    private BattlemetricsService battlemetricsService;
    private Configuration configuration;
    private NextLayer nextLayer;
    private ServerInfo serverInfo;

    private final String TASK_SCHEDULER_EXCEPTION = "TASK_SCHEDULER_EXCEPTION";

    @Autowired
    public TaskScheduler(BattlemetricsService battlemetricsService, Configuration configuration, NextLayer nextLayer, ServerInfo serverInfo) {
        this.battlemetricsService = battlemetricsService;
        this.configuration = configuration;
        this.nextLayer = nextLayer;
        this.serverInfo = serverInfo;
    }

    //TODO Use Spring Scheduler

    private final ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(1);

    public void startScheduleTask(JDA jda) {

        final ScheduledFuture<?> taskHandle = scheduler.scheduleWithFixedDelay(
                () -> {
                    try {

                        EmbedBuilder eb = battlemetricsService.getServerInfo();

                        if (configuration.isProd == 1) {
                            //jgkp-server
                            Objects.requireNonNull(jda.getTextChannelById(configuration.getJgkpTextchannelId())).retrieveMessageById(configuration.getJgkpMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).queue();
                            });

                            //dsg-server
                            Objects.requireNonNull(jda.getTextChannelById(configuration.getDsgTextchannelId())).retrieveMessageById(configuration.getDsgMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).queue();
                            });

                            System.out.println("Updated Embed (prod)");

                        } else if (configuration.isProd == 0) {
                            //test-server
                            Objects.requireNonNull(jda.getTextChannelById(configuration.getTestTextchannelId())).retrieveMessageById(configuration.getTestMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).queue();
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
