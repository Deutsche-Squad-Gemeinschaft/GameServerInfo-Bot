package serverInfoBot.api;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import serverInfoBot.api.model.ServerInfo;
import serverInfoBot.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

@RestController
public class BattlemetricsController {

    private Configuration configuration;

    @Autowired
    public BattlemetricsController(Configuration configuration) {
        this.configuration = configuration;
    }

    public ServerInfo getData() throws IOException {

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



        // Construct the URL to send the RCON command
        String rconUrl = "https://api.battlemetrics.com/servers/3219649/command";

        // Set up the HTTP connection
        URL url = new URL(rconUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", configuration.getBattlemetricsShowNextMapAPIToken());
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // Construct the JSON payload for the RCON command
        String jsonPayload = "{\"data\":{\"type\":\"rconCommand\",\"attributes\":{\"command\":\"squad:showNextMap\"}}}";

        // Send the JSON payload as the HTTP request body
        con.getOutputStream().write(jsonPayload.getBytes("UTF-8"));

        // Read the response from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String responseNextLayer = in.readLine();

        in.close();

        // Convert the response to a JSON object
        JSONObject jsonResponse = new JSONObject(responseNextLayer);

        String nextLayer = jsonResponse.getJSONObject("data").getJSONObject("attributes").getString("result");


        return ServerInfo.builder()
                .name(name)
                .players(players)
                .status(status)
                .map(map)
                .playTime(playTime)
                .pubQueue(pubQueue)
                .resQueue(resQueue)
                .teamOne(teamOne)
                .teamTwo(teamTwo)
                .nextLayer(nextLayer)
                .build();
    }
}
