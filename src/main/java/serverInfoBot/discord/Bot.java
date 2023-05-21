package serverInfoBot.discord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serverInfoBot.config.Configuration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Getter
@Setter
public class Bot {

    private final Configuration configuration;
    private final EventHandler eventHandler;
    private JDA jda;

    public JDA startBot() throws InterruptedException {

        JDA jda = JDABuilder.createDefault(configuration.getBotToken())
                .addEventListeners(eventHandler)
                .setActivity(Activity.playing("#info"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build()
                .awaitReady();

        setJda(jda);
        System.out.println("Im online.");

        return jda;
    }
}