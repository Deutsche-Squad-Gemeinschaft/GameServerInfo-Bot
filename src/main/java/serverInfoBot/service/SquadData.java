package serverInfoBot.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Getter
@Setter
@Service
public class SquadData {

    private ArrayList<String> validTeamNames;
    private ArrayList<ArrayList<String>> mapData;

    public void loadValidTeamNames(){
        ArrayList<String> validTeamNames = new ArrayList<>();
        validTeamNames.add(0, "USA");
        validTeamNames.add(1, "USMC");
        validTeamNames.add(2, "AUS");
        validTeamNames.add(3, "RUS");
        validTeamNames.add(4, "RU");
        validTeamNames.add(5, "GB");
        validTeamNames.add(6, "INS");
        validTeamNames.add(7, "MIL");
        validTeamNames.add(8, "MEA");
        validTeamNames.add(9, "CAF");
        setValidTeamNames(validTeamNames);
        System.out.println("Loaded validTeamNames");
    }

    public void loadMapData(){
        ArrayList<String> alBasrah = new ArrayList<>();
        ArrayList<String> anvil = new ArrayList<>();
        ArrayList<String> belaya = new ArrayList<>();
        ArrayList<String> blackCoast = new ArrayList<>();
        ArrayList<String> chora = new ArrayList<>();
        ArrayList<String> fallujah = new ArrayList<>();
        ArrayList<String> foolsRoad = new ArrayList<>();
        ArrayList<String> gooseBay = new ArrayList<>();
        ArrayList<String> gorodok = new ArrayList<>();
        ArrayList<String> kamdesh = new ArrayList<>();
        ArrayList<String> kohat = new ArrayList<>();
        ArrayList<String> kokan = new ArrayList<>();
        ArrayList<String> lashkar = new ArrayList<>();
        ArrayList<String> logar = new ArrayList<>();
        ArrayList<String> manic = new ArrayList<>();
        ArrayList<String> mestia = new ArrayList<>();
        ArrayList<String> mutaha = new ArrayList<>();
        ArrayList<String> narva = new ArrayList<>();
        ArrayList<String> skorpo = new ArrayList<>();
        ArrayList<String> sumari = new ArrayList<>();
        ArrayList<String> tallil = new ArrayList<>();
        ArrayList<String> yehorivka = new ArrayList<>();

        alBasrah.add(0, "ALBASRAH");
        alBasrah.add(1, "https://i.imgur.com/jqgz2mR.png");

        anvil.add(0, "ANVIL");
        anvil.add(1, "https://i.imgur.com/PchI6rL.png");

        belaya.add(0, "BELAYA");
        belaya.add(1, "https://i.imgur.com/8xKO3qr.png");

        blackCoast.add(0, "BLACKCOAST");
        blackCoast.add(1, "https://i.imgur.com/ArkkhKD.png");

        chora.add(0, "CHORA");
        chora.add(1, "https://i.imgur.com/FqCJiyK.png");

        fallujah.add(0, "FALLUJAH");
        fallujah.add(1, "https://i.imgur.com/BByGaU4.png");

        foolsRoad.add(0, "FOOLSROAD");
        foolsRoad.add(1, "https://i.imgur.com/WU71qoq.png");

        gooseBay.add(0, "GOOSEBAY");
        gooseBay.add(1, "https://i.imgur.com/JRDgy43.png");

        gorodok.add(0, "GORODOK");
        gorodok.add(1, "https://i.imgur.com/G2DyEmB.png");

        kamdesh.add(0, "KAMDESH");
        kamdesh.add(1, "https://i.imgur.com/8agCJms.png");

        kohat.add(0, "KOHAT");
        kohat.add(1, "https://i.imgur.com/D5RWgkB.png");

        kokan.add(0, "KOKAN");
        kokan.add(1, "https://i.imgur.com/Gq8Zuwz.png");

        lashkar.add(0, "LASHKAR");
        lashkar.add(1, "https://i.imgur.com/cKVwTxV.png");

        logar.add(0, "LOGAR");
        logar.add(1, "https://i.imgur.com/DAQ82Lc.png");

        manic.add(0, "MANIC");
        manic.add(1, "https://i.imgur.com/bpG0QTh.png");

        mestia.add(0, "MESTIA");
        mestia.add(1, "https://i.imgur.com/X2wt9Vu.png");

        mutaha.add(0, "MUTAHA");
        mutaha.add(1, "https://i.imgur.com/L4Ykq9z.png");

        narva.add(0, "NARVA");
        narva.add(1, "https://i.imgur.com/xq6DF6H.png");

        skorpo.add(0, "SKORPO");
        skorpo.add(1, "https://i.imgur.com/2Spzmr4.png");

        sumari.add(0, "SUMARI");
        sumari.add(1, "https://i.imgur.com/5s0puaf.png");

        tallil.add(0, "TALLIL");
        tallil.add(1, "https://i.imgur.com/buPnnI4.png");

        yehorivka.add(0, "YEHORIVKA");
        yehorivka.add(1, "https://i.imgur.com/t8Rkm76.png");

        ArrayList<ArrayList<String>> mapData = new ArrayList<>();

        mapData.add(0, alBasrah);
        mapData.add(1, anvil);
        mapData.add(2, belaya);
        mapData.add(3, blackCoast);
        mapData.add(4, chora);
        mapData.add(5, fallujah);
        mapData.add(6, foolsRoad);
        mapData.add(7, gooseBay);
        mapData.add(8, gorodok);
        mapData.add(9, kamdesh);
        mapData.add(10, kohat);
        mapData.add(11, kokan);
        mapData.add(12, lashkar);
        mapData.add(13, logar);
        mapData.add(14, manic);
        mapData.add(15, mestia);
        mapData.add(16, mutaha);
        mapData.add(17, narva);
        mapData.add(18, skorpo);
        mapData.add(19, sumari);
        mapData.add(20, tallil);
        mapData.add(21, yehorivka);

        setMapData(mapData);
        System.out.println("Loaded mapData");
    }
}
