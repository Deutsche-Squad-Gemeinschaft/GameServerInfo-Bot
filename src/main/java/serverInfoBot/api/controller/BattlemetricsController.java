package serverInfoBot.api.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.config.Configuration;

import java.io.IOException;
import java.util.Collections;

@RestController
public class BattlemetricsController {

    private Configuration configuration;
    private ServerInfo serverInfo;

    @Autowired
    public BattlemetricsController(Configuration configuration, ServerInfo serverInfo) {
        this.configuration = configuration;
        this.serverInfo = serverInfo;
    }

    public void getData() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", configuration.getBattlemetricsApiToken());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String resourceURL = "https://api.battlemetrics.com/servers/3219649";
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> response = restTemplate.exchange(resourceURL, HttpMethod.GET, entity, String.class);

        String data = response.getBody();

        JSONObject obj = new JSONObject(data);

        String name = obj.getJSONObject("data").getJSONObject("attributes").getString("name");
        int players = obj.getJSONObject("data").getJSONObject("attributes").getInt("players");
        String status = obj.getJSONObject("data").getJSONObject("attributes").getString("status");
        String map = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getString("map");
        int playTime  = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getInt("squad_playTime");
        int pubQueue  = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getInt("squad_publicQueue");
        int resQueue  = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getInt("squad_reservedQueue");
        String teamOne = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getString("squad_teamOne");
        String teamTwo = obj.getJSONObject("data").getJSONObject("attributes").getJSONObject("details").getString("squad_teamTwo");

        serverInfo.setName(name);
        serverInfo.setPlayers(players);
        serverInfo.setStatus(status);
        serverInfo.setLayer(map);
        serverInfo.setPlayTime(playTime);
        serverInfo.setPubQueue(pubQueue);
        serverInfo.setResQueue(resQueue);
        serverInfo.setTeamOne(teamOne);
        serverInfo.setTeamTwo(teamTwo);
    }
}
