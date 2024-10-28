package info.preva1l.slave.pubsub.messages;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
public class TicketMessage extends TicketAction {
    @Expose
    private final String message;

    public TicketMessage(long ticketId, long senderId, String message) {
        super(ticketId, senderId);
        this.message = message;
    }
}
