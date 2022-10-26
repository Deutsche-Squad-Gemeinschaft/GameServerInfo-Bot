package serverInfoBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import serverInfoBot.service.StartService;


@Configuration
@ConfigurationPropertiesScan
@ComponentScan
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class ServerInfoBotApplication {

	private static StartService startService;

	@Autowired
	public ServerInfoBotApplication(StartService startService) {
		ServerInfoBotApplication.startService = startService;
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(ServerInfoBotApplication.class, args);
		startService.start();
	}
}
