package serverInfoBot.service;

import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import serverInfoBot.db.entities.LayerInformation;
import serverInfoBot.db.entities.MatchHistory;
import serverInfoBot.db.repositories.LayerInformationRepository;
import serverInfoBot.db.repositories.MatchHistoryRepository;

import java.util.*;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final MatchHistoryRepository matchHistoryRepository;
    private final LayerInformationRepository layerInformationRepository;

    public String getMapStatistics(List<String> dates) {
        List<MatchHistory> matchHistories = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            List<MatchHistory> matchHistorie = matchHistoryRepository.findByStartDateAndFlag(dates.get(i), "Live");
            matchHistories.addAll(matchHistorie);
        }

        HashMap<String, Integer> countedMaps = countMaps(matchHistories);

        Map<String, Integer> sortedMapAsc = sortByComparator(countedMaps, false);

        String mapStatistics = "";

        for (Map.Entry<String, Integer> entry : sortedMapAsc.entrySet()) {

            int counter = entry.getValue();
            String mapName = entry.getKey();
            mapStatistics = mapStatistics.concat("**" + mapName + ":** ...." + counter + "\n\n");
        }

        return mapStatistics;
    }

    public ArrayList<String> getLayerStatistics(List<String> dates) {
        List<MatchHistory> matchHistories = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            List<MatchHistory> matchHistorie = matchHistoryRepository.findByStartDateAndFlag(dates.get(i), "Live");
            matchHistories.addAll(matchHistorie);
        }

        HashMap<String, Integer> countedLayer = countLayer(matchHistories);

        String layerStatistics = "";

        TreeMap<String, Integer> orderedMap = new TreeMap<>(countedLayer);

        ArrayList<String> layerStatisticsList = new ArrayList<>();
        int i = 0;

        for (String layerName : orderedMap.keySet()) {

            int counter = orderedMap.get(layerName);
            layerStatistics = layerStatistics.concat("**" + layerName + ":** ...." + counter + "\n\n");

            if (layerStatistics.length() > 3500) {
                layerStatisticsList.add(i, layerStatistics);
                i = i + 1;
                layerStatistics = "";
            }
        }

        if (layerStatisticsList.size() == 0) {
            layerStatisticsList.add(layerStatistics);
        }

        return layerStatisticsList;
    }

    public String getGamemodeStatistics(List<String> dates) {
        List<MatchHistory> matchHistories = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            List<MatchHistory> matchHistorie = matchHistoryRepository.findByStartDateAndFlag(dates.get(i), "Live");
            matchHistories.addAll(matchHistorie);
        }

        HashMap<String, Integer> countedGamemode = countGamemode(matchHistories);

        Map<String, Integer> sortedMapAsc = sortByComparator(countedGamemode, false);

        String gamemodeStatistics = "";

        for (Map.Entry<String, Integer> entry : sortedMapAsc.entrySet()) {

            int counter = entry.getValue();
            String gamemodeName = entry.getKey();
            gamemodeStatistics = gamemodeStatistics.concat("**" + gamemodeName + ":** ...." + counter + "\n\n");
        }

        return gamemodeStatistics;
    }

    public List<String> getDates(int days) {

        List<String> dates = new ArrayList<>();

        DateTime dt = new DateTime();
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
        dt = dt.withZone(timeZone);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
        String currentDate = fmt.print(dt);
        dates.add(currentDate);

        for (int i = 1; i < days; i++) {
            dates.add(fmt.print(dt.minusDays(i)));
        }

        return dates;
    }

    private HashMap<String, Integer> countMaps(List<MatchHistory> matchHistories) {
        HashMap<String, Integer> countedMaps = new HashMap<>();

        for (MatchHistory matchHistory : matchHistories) {
            String mapName = matchHistory.getMapName();

            if (countedMaps.containsKey(mapName)) {
                int oldValue = countedMaps.get(mapName);
                countedMaps.replace(mapName, oldValue + 1);
            } else {
                countedMaps.put(mapName, 1);
            }
        }
        return countedMaps;
    }

    private HashMap<String, Integer> countLayer(List<MatchHistory> matchHistories) {
        HashMap<String, Integer> countedLayer = new HashMap<>();

        for (MatchHistory matchHistory : matchHistories) {
            String layerName = matchHistory.getLayerName();

            if (!countedLayer.containsKey(layerName)) {
                countedLayer.put(layerName, 1);
            } else {
                int oldValue = countedLayer.get(layerName);
                countedLayer.replace(layerName, oldValue + 1);
            }
        }
        return countedLayer;
    }

    private HashMap<String, Integer> countGamemode(List<MatchHistory> matchHistories) {
        HashMap<String, Integer> countedGamemode = new HashMap<>();
        String gamemode = "";

        for (MatchHistory matchHistory : matchHistories) {
            String layerName = matchHistory.getLayerName();
            if (layerName.contains("RAAS")) {
                gamemode = "RAAS";
            } else if (layerName.contains("AAS")) {
                gamemode = "AAS";
            } else if (layerName.contains("Invasion")) {
                gamemode = "Invasion";
            } else if (layerName.contains("TC")) {
                gamemode = "TC";
            } else if (layerName.contains("Destruction")) {
                gamemode = "Destruction";
            } else if (layerName.contains("Insurgency")) {
                gamemode = "Insurgency";
            }

            if (countedGamemode.containsKey(gamemode)) {
                int oldValue = countedGamemode.get(gamemode);
                countedGamemode.replace(gamemode, oldValue + 1);
            } else {
                countedGamemode.put(gamemode, 1);
            }
        }
        return countedGamemode;
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
