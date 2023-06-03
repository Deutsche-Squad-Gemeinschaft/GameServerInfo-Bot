package serverInfoBot.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import serverInfoBot.db.entities.MatchHistory;
import serverInfoBot.db.repositories.FlagTimeInformationRepository;
import serverInfoBot.db.repositories.MatchHistoryRepository;
import serverInfoBot.service.StatisticsService;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventHandler extends ListenerAdapter {

    private final MatchHistoryRepository matchHistoryRepository;
    private final FlagTimeInformationRepository flagTimeInformationRepository;
    private final StatisticsService statisticsService;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("Match-Start Benachrichtigung")) {
            if (!event.getMember().getRoles().contains(event.getGuild().getRoleById(event.getGuild().getRolesByName("Match-Start Notification", false).get(0).getIdLong()))) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(event.getGuild().getRolesByName("Match-Start Notification", false).get(0).getIdLong())).queue();
                event.reply("Du wirst nun beim Start des nächsten Matches hier im Kanal gepingt!").setEphemeral(true).queue();
            } else {
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

            if (!validateDate(date)) {
                event.reply("Das Datum muss im Format tt.MM.jjjj sein! Bitte versuche es erneut.").setEphemeral(true).queue();
                return;
            }

            List<MatchHistory> matchHistory = matchHistoryRepository.findByStartDateAndFlag(date, "Live");

            if (matchHistory.size() == 0) {
                event.reply("Zu diesem Datum habe ich leider keine Daten.").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Match Historie " + date, null);

            eb.setColor(new Color(255, 196, 12));

            for (MatchHistory match : matchHistory) {
                String matchStart = match.getStartTime() + " Uhr";
                String matchEnde = match.getEndTime() + " Uhr";
                String layer = match.getLayerName();

                if (matchEnde.equals("null Uhr")) {
                    matchEnde = "läuft";
                }

                eb.addField(layer, "Match-Start: " + matchStart + "\nMatch-Ende: " + matchEnde, false);
            }

            eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
            eb.setTimestamp(Instant.now());

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } else if (event.getName().equals("cia")) {
            int trackedDays = flagTimeInformationRepository.findAll().size();
            int trackedLiveMatches = matchHistoryRepository.findByFlag("Live").size();

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("CIA", null);

            eb.setColor(new Color(255, 196, 12));

            eb.addField("Bisher getrackte Live-Matches: ", String.valueOf(trackedLiveMatches), false);
            eb.addField("Bisher getrackte Tage: ", String.valueOf(trackedDays), false);
            eb.addField("Durchschn. Live-Matches / Tag: ", String.valueOf(trackedLiveMatches / trackedDays), false);

            eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
            eb.setTimestamp(Instant.now());

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } else if (event.getName().equals("map-statistiken")) {

            OptionMapping daysOption = event.getOption("tage");

            assert daysOption != null;
            int days = daysOption.getAsInt() + 1;

            String mapStatistics = statisticsService.getMapStatistics(statisticsService.getDates(days));

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Map Statistiken der letzten " + (days-1) + " Tage", null);

            eb.setColor(new Color(255, 196, 12));

            eb.setDescription("Es werden ausschließlich Maps mit gespielten Live-Runden gezählt. Maps, die nicht aufgelistet sind, wurden in den letzten " + (days-1) + " Tagen nicht in Live-Runden gespielt. \n\n" + mapStatistics);

            eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
            eb.setTimestamp(Instant.now());

            if (mapStatistics.length() == 0) {
                event.reply("Zu diesen Tagen habe ich leider keine Daten.").setEphemeral(true).queue();
                return;
            }

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } else if (event.getName().equals("layer-statistiken")) {

            OptionMapping daysOption = event.getOption("tage");

            assert daysOption != null;
            int days = daysOption.getAsInt() + 1;

            ArrayList<String> layerStatisticsList = statisticsService.getLayerStatistics(statisticsService.getDates(days));
            List<MessageEmbed> embeds = new ArrayList<>();

            for (int i = 0; i < layerStatisticsList.size(); i++) {
                EmbedBuilder eb = new EmbedBuilder();

                eb.setTitle("Layer Statistiken der letzten " + (days-1) + " Tage (Nachricht "+ (i +1) + " von " + layerStatisticsList.size() + ")", null);

                eb.setColor(new Color(255, 196, 12));

                eb.setDescription("Es werden ausschließlich Layer mit gespielten Live-Runden gezählt. Layer, die nicht aufgelistet sind, wurden in den letzten " + (days-1) + " Tagen nicht in Live-Runden gespielt. \n\n" + layerStatisticsList.get(i));

                eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
                eb.setTimestamp(Instant.now());

                embeds.add(eb.build());
            }
            event.getInteraction().replyEmbeds(embeds).setEphemeral(true).queue();

        }else if (event.getName().equals("gamemode-statistiken")) {
            OptionMapping daysOption = event.getOption("tage");

            assert daysOption != null;
            int days = daysOption.getAsInt() + 1;

            String gamemodeStatistics = statisticsService.getGamemodeStatistics(statisticsService.getDates(days));

            EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle("Gamemode Statistiken der letzten " + (days-1) + " Tage", null);

            eb.setColor(new Color(255, 196, 12));

            eb.setDescription("Es werden ausschließlich Gamemodi zu gespielten Live-Runden gezählt. Gamemodi, die nicht aufgelistet sind, wurden in den letzten " + (days-1) + " Tagen nicht in Live-Runden gespielt. \n\n" + gamemodeStatistics);

            eb.setFooter("© official DSG Bot", "https://dsg-gaming.de/images/og.jpg");
            eb.setTimestamp(Instant.now());

            if (gamemodeStatistics.length() == 0) {
                event.reply("Zu diesen Tagen habe ich leider keine Daten.").setEphemeral(true).queue();
                return;
            }

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

        commandData.add(Commands.slash("cia", "Bekomme die Anzahl bisher getrackter Tage und Matches"));

        commandData.add(Commands.slash("map-statistiken", "Bekomme eine Statistik wie oft welche Live-Map gespielt wurde in den letzten x Tagen")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "tage", "Die Anzahl der vergangenen Tage zu denen die Statistik erstellt werden soll", true).setMinValue(1).setMaxValue(14)));

        commandData.add(Commands.slash("layer-statistiken", "Bekomme eine Statistik wie oft welche Live-Layer gespielt wurden in den letzten x Tagen")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "tage", "Die Anzahl der vergangenen Tage zu denen die Statistik erstellt werden soll", true).setMinValue(1).setMaxValue(14)));

        commandData.add(Commands.slash("gamemode-statistiken", "Bekomme eine Statistik wie oft welcher Live-Gamemode gespielt wurden in den letzten x Tagen")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "tage", "Die Anzahl der vergangenen Tage zu denen die Statistik erstellt werden soll", true).setMinValue(1).setMaxValue(14)));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    private boolean validateDate(String date) {
        if (date.length() == 10) {
            if (Integer.parseInt(date.substring(0, 2)) < 32 && Integer.parseInt(date.substring(0, 2)) > 0) {
                if (Integer.parseInt(date.substring(3, 5)) < 13 && Integer.parseInt(date.substring(3, 5)) > 0) {
                    if (Integer.parseInt(date.substring(6, 10)) > 2020) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
