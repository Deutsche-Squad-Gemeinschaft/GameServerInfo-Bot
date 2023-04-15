package serverInfoBot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@org.springframework.context.annotation.Configuration
@PropertySource("classpath:application-prod.properties")
@ConfigurationProperties(prefix ="serverinfobot")
public class Configuration {
    @NotBlank
    public int isProd;
    @NotBlank
    private String botToken;
    @NotBlank
    private String battlemetricsApiToken;
    @NotBlank
    private String jgkpTextchannelId;
    @NotBlank
    private String jgkpMessageId;
    @NotBlank
    private String dsgTextchannelId;
    @NotBlank
    private String dsgMessageId;
    @NotBlank
    private String testTextchannelId;
    @NotBlank
    private String testMessageId;
    @NotBlank
    private String battlemetricsShowNextMapAPIToken;
}
