package serverInfoBot.service;

import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.discord.Bot;

@Service
public class StartService {

    private Bot bot;
    private TaskScheduler taskScheduler;

    @Autowired
    public StartService(Bot bot, TaskScheduler taskScheduler) {
        this.bot = bot;
        this.taskScheduler = taskScheduler;
    }

    public void start() throws InterruptedException {
        JDA jda = bot.startBot();
        taskScheduler.startScheduleTaskNextLayer();
        taskScheduler.startScheduleTask(jda);
    }
}
