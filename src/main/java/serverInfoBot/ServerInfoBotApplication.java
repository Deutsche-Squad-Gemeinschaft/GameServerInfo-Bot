package serverInfoBot;

import net.dv8tion.jda.api.JDA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import serverInfoBot.discord.Bot;
import serverInfoBot.service.TaskScheduler;


@Configuration
@ConfigurationPropertiesScan
@ComponentScan
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class ServerInfoBotApplication {

	public ServerInfoBotApplication() {
	}

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext applicationContext = SpringApplication.run(ServerInfoBotApplication.class, args);

		Bot bot = applicationContext.getBean(Bot.class);
		TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);

		JDA jda = bot.startBot();
		taskScheduler.startScheduleTaskNextLayer();
		taskScheduler.startScheduleTask(jda);
		taskScheduler.updateFlagTimeInformationInPanel();
	}
}
