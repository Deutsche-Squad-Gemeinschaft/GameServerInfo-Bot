package serverInfoBot.customExceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandledException extends Exception{

    private String code;

    public HandledException(String code, String message) {
        super(message);
        this.setCode(code);
    }
}
