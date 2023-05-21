package serverInfoBot.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import serverInfoBot.db.entities.MatchHistory;
import serverInfoBot.db.repositories.MatchHistoryRepository;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventHandler extends ListenerAdapter {

    private final MatchHistoryRepository matchHistoryRepository;

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

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("vorherige-matches")) {

            OptionMapping dateOption = event.getOption("datum");

            assert dateOption != null;
            String date = dateOption.getAsString();

            if (!validateDate(date)){
                event.reply("Das Datum muss im Format tt.MM.jjjj sein! Bitte versuche es erneut.").setEphemeral(true).queue();
                return;
            }

            List<MatchHistory> matchHistory = matchHistoryRepository.findByStartDateAndFlag(date, "Live");

            if (matchHistory.size() == 0){
                event.reply("Zu diesem Datum habe ich leider keine Daten.").setEphemeral(true).queue();
                return;
            }

                EmbedBuilder eb = new EmbedBuilder();

                eb.setTitle("Match Historie " + date, null);

                eb.setColor(new Color(255, 196, 12));

            for (MatchHistory match : matchHistory) {
                String matchStart = match.getStartTime()+ " Uhr";
                String matchEnde = match.getEndTime() + " Uhr";
                String layer = match.getLayerName();

                if (matchEnde.equals("null Uhr")){
                    matchEnde = "läuft";
                }

                eb.addField(layer, "Match-Start: " + matchStart + "\nMatch-Ende: " + matchEnde, false);
            }

                eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
                eb.setTimestamp(Instant.now());

                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }


    @Override
    public void onGuildReady(GuildReadyEvent event) {

        //Server-Only
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("vorherige-matches", "Bekomme die gespielten Live-Runden eines bestimmten Tages")
                .addOptions(
                        new OptionData(OptionType.STRING, "datum", "Das Datum von dem du die Matches bekommen möchtest. Format: tt.MM.jjjj", true)));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    private boolean validateDate(String date){
        if(date.length() == 10){
            if (Integer.parseInt(date.substring(0,2)) < 32 && Integer.parseInt(date.substring(0,2)) > 0){
                if (Integer.parseInt(date.substring(3,5)) < 13 && Integer.parseInt(date.substring(3,5)) > 0){
                    if (Integer.parseInt(date.substring(6,10)) > 2020){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
