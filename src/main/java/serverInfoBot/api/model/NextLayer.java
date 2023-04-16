package serverInfoBot.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@EqualsAndHashCode
@Component
public class NextLayer {
    private String nextLayer;
    private boolean executedFirstTime = false;
}
