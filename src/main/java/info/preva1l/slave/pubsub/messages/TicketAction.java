package info.preva1l.slave.pubsub.messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketAction {
    @Expose @SerializedName("ticket_id")
    private final long ticketId;
    @Expose @SerializedName("sender_id")
    private final long senderId;
}
