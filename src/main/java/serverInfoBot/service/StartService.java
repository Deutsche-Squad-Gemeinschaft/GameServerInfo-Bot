package serverInfoBot.service;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;
import serverInfoBot.discord.Bot;

@RequiredArgsConstructor
@Service
public class StartService {

    private final Bot bot;
    private final TaskScheduler taskScheduler;

    public void start() throws InterruptedException {
        JDA jda = bot.startBot();
        taskScheduler.startScheduleTaskNextLayer();
        taskScheduler.startScheduleTask(jda);
        taskScheduler.updateFlagTimeInformationInPanel();
    }
}
