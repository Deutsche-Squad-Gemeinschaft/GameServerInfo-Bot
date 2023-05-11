package serverInfoBot.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.config.Configuration;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;


@Service
public class Bot {

    private Configuration configuration;

    @Autowired
    public Bot(Configuration configuration) {
       this.configuration = configuration;
    }

    public JDA startBot() throws InterruptedException {

        JDA jda = JDABuilder.createDefault(configuration.getBotToken())
                .setActivity(Activity.playing("#info"))
                .build()
                .awaitReady();

        System.out.println("Im online.");

        return jda;
    }
}