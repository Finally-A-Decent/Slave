package info.preva1l.slave.pubsub;

import com.google.gson.annotations.Expose;
import lombok.*;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class Message {
    @Expose private Payload payload;
    @Expose private Type type;

    public void send(Broker broker) {
        if (broker == null) return;
        broker.send(this);
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        BOT_BOUND_TICKET_CREATE(true),
        WEB_BOUND_TICKET_CREATE,

        BOT_BOUND_TICKET_CLOSE(true),
        WEB_BOUND_TICKET_CLOSE,

        BOT_BOUND_TICKET_MESSAGE(true),
        WEB_BOUND_TICKET_MESSAGE,

        BOT_BOUND_TICKET_LOCK(true),
        WEB_BOUND_TICKET_LOCK,
        ;

        private final boolean botBound;

        Type() {
            this.botBound = false;
        }
    }
}
