package serverInfoBot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application-prod.properties")
@ConfigurationProperties(prefix ="serverinfobot")
public class Configuration {

    public int isProd;
    private String botToken;
    private String battlemetricsApiToken;
    private int sendNewMessages;
    private String battlemetricsShowNextMapAPIToken;
}
