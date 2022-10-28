package serverInfoBot.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceMaps {
    private String mapName;
    private String imageLink;

    public ServiceMaps(String mapName, String imageLink) {
        this.mapName = mapName;
        this.imageLink = imageLink;
    }
}
