package serverInfoBot.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.config.Configuration;
import serverInfoBot.customExceptions.HandledException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TaskScheduler {

    private BattlemetricsService battlemetricsService;
    private Configuration configuration;

    private final String TASK_SCHEDULER_EXCEPTION = "TASK_SCHEDULER_EXCEPTION";

    @Autowired
    public TaskScheduler(BattlemetricsService battlemetricsService, Configuration configuration) {
        this.battlemetricsService = battlemetricsService;
        this.configuration = configuration;
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
                            jda.getTextChannelById(configuration.getJgkpTextchannelId()).retrieveMessageById(configuration.getJgkpMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).queue();
                            });

                            //dsg-server
                            jda.getTextChannelById(configuration.getDsgTextchannelId()).retrieveMessageById(configuration.getDsgMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).queue();
                            });

                            System.out.println("Updated Embed (prod)");

                        } else if (configuration.isProd == 0) {
                            //test-server
                            jda.getTextChannelById(configuration.getTestTextchannelId()).retrieveMessageById(configuration.getTestMessageId()).queue(message -> {
                                message.editMessageEmbeds(eb.build()).queue();
                            });

                            System.out.println("Updated Embed (localdev)");
                        }else {
                            throw new HandledException(TASK_SCHEDULER_EXCEPTION, "Failed to select Embed Destinations");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 0, 3, TimeUnit.MINUTES);
    }
}
