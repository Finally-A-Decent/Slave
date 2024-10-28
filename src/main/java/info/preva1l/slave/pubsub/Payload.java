package info.preva1l.slave.pubsub;

import com.google.gson.annotations.Expose;
import info.preva1l.slave.pubsub.messages.TicketAction;
import info.preva1l.slave.pubsub.messages.TicketMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Payload {
    @Nullable
    @Expose
    private TicketAction ticketAction;

    @Nullable
    @Expose
    private TicketMessage ticketMessage;

    /**
     * Returns an empty cross-server message payload.
     *
     * @return an empty payload
     */
    @NotNull
    public static Payload empty() {
        return new Payload();
    }

    /**
     * Returns a payload containing a {@link UUID}.
     *
     * @param action the action to send
     * @return a payload containing the uuid
     */
    @NotNull
    public static Payload withTicketAction(@NotNull TicketAction action) {
        final Payload payload = new Payload();
        payload.ticketAction = action;
        return payload;
    }

    /**
     * Returns a payload containing a message and a recipient.
     *
     * @param message the message to send
     * @return a payload containing the message
     */
    @NotNull
    public static Payload withTicketMessage(@NotNull TicketMessage message) {
        final Payload payload = new Payload();
        payload.ticketMessage = message;
        return payload;
    }

    public Optional<TicketMessage> getTicketMessage() {
        return Optional.ofNullable(ticketMessage);
    }

    public Optional<TicketAction> getTicketAction() {
        return Optional.ofNullable(ticketAction);
    }
}
