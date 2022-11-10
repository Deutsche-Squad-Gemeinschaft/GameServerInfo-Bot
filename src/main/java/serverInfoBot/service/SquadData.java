package serverInfoBot.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Service
public class SquadData {

    private List<String> validTeamNames;
    private List<ServiceMaps> mapData;

    public void loadValidTeamNames(){
        List<String> validTeamNames = Arrays.asList("USA", "USMC", "AUS", "RUS", "RU", "GB", "INS", "MIL", "MEA", "CAF");
        setValidTeamNames(validTeamNames);
        System.out.println("Loaded validTeamNames");
    }

    public void loadMapData(){
        List<ServiceMaps> mapData = new ArrayList<>();

        mapData.add(new ServiceMaps("ALBASRAH", "https://i.imgur.com/jqgz2mR.png"));
        mapData.add(new ServiceMaps("ANVIL", "https://i.imgur.com/PchI6rL.png"));
        mapData.add(new ServiceMaps("BELAYA", "https://i.imgur.com/8xKO3qr.png"));
        mapData.add(new ServiceMaps("BLACKCOAST", "https://i.imgur.com/ArkkhKD.png"));
        mapData.add(new ServiceMaps("CHORA", "https://i.imgur.com/FqCJiyK.png"));
        mapData.add(new ServiceMaps("FALLUJAH", "https://i.imgur.com/BByGaU4.png"));
        mapData.add(new ServiceMaps("FOOLSROAD", "https://i.imgur.com/WU71qoq.png"));
        mapData.add(new ServiceMaps("GOOSEBAY", "https://i.imgur.com/JRDgy43.png"));
        mapData.add(new ServiceMaps("GORODOK", "https://i.imgur.com/G2DyEmB.png"));
        mapData.add(new ServiceMaps("HARJU", "https://i.imgur.com/OofCHFN.png"));
        mapData.add(new ServiceMaps("KAMDESH", "https://i.imgur.com/8agCJms.png"));
        mapData.add(new ServiceMaps("KOHAT", "https://i.imgur.com/D5RWgkB.png"));
        mapData.add(new ServiceMaps("KOKAN", "https://i.imgur.com/Gq8Zuwz.png"));
        mapData.add(new ServiceMaps("LASHKAR", "https://i.imgur.com/cKVwTxV.png"));
        mapData.add(new ServiceMaps("LOGAR", "https://i.imgur.com/DAQ82Lc.png"));
        mapData.add(new ServiceMaps("MANIC", "https://i.imgur.com/bpG0QTh.png"));
        mapData.add(new ServiceMaps("MESTIA", "https://i.imgur.com/X2wt9Vu.png"));
        mapData.add(new ServiceMaps("MUTAHA", "https://i.imgur.com/L4Ykq9z.png"));
        mapData.add(new ServiceMaps("NARVA", "https://i.imgur.com/xq6DF6H.png"));
        mapData.add(new ServiceMaps("SKORPO", "https://i.imgur.com/2Spzmr4.png"));
        mapData.add(new ServiceMaps("SUMARI", "https://i.imgur.com/5s0puaf.png"));
        mapData.add(new ServiceMaps("TALLIL", "https://i.imgur.com/buPnnI4.png"));
        mapData.add(new ServiceMaps("YEHORIVKA", "https://i.imgur.com/t8Rkm76.png"));

        setMapData(mapData);
        System.out.println("Loaded mapData");
    }
}
