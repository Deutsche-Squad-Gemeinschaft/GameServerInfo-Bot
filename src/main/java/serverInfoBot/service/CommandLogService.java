package serverInfoBot.service;

import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import serverInfoBot.db.entities.CommandLog;
import serverInfoBot.db.repositories.CommandLogRepository;

@RequiredArgsConstructor
@Service
public class CommandLogService {
    private final CommandLogRepository commandLogRepository;

    public enum EventType {
        MATCHNOTIFICATION,
        VORHERIGEMATCHES,
        CIA,
        GAMEMODESTATISTICS,
        MAPSTATISTICS,
        LAYERSTATISTICS
    }

    public void logEvent(String userName, EventType event, String eventDays){
        CommandLog commandLog = new CommandLog();

        commandLog.setUserName(userName);
        commandLog.setEvent(event.toString());

        DateTime dt = new DateTime();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        dt = dt.withZone(timeZone);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
        String dateTime = fmt.print(dt);

        commandLog.setDateTime(dateTime);
        commandLog.setEventDays(eventDays);

        commandLogRepository.save(commandLog);
    }
}
