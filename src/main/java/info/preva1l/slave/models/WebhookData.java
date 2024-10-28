package info.preva1l.slave.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebhookData {
    private final int id;
    private final String token;
}
