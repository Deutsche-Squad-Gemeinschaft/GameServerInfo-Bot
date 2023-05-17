package serverInfoBot.discord;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class EventHandler extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("Match-Start Benachrichtigung")) {
            if(!event.getMember().getRoles().contains(event.getGuild().getRoleById(event.getGuild().getRolesByName("Match-Start Notification", false).get(0).getIdLong()))){
                event.getGuild().addRoleToMember(event.getMember(),event.getGuild().getRoleById(event.getGuild().getRolesByName("Match-Start Notification", false).get(0).getIdLong())).queue();
                event.reply("Du wirst nun beim Start des nächsten Matches hier im Kanal gepingt!").setEphemeral(true).queue();
            }else {
                event.reply("Du wirst bereits beim Start des nächsten Matches gepingt!").setEphemeral(true).queue();
            }

        }
    }
}
