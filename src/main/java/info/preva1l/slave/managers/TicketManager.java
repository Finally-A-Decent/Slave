package info.preva1l.slave.managers;

import info.preva1l.slave.models.tickets.Ticket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketManager implements EventListener {
    private static TicketManager instance;

    private final Map<Long, Ticket> tickets = new ConcurrentHashMap<>();

    public static TicketManager getInstance() {
        if (instance == null) {
            instance = new TicketManager();
        }
        return instance;
    }

    public void cacheTicket(Ticket ticket) {
        tickets.put(ticket.getChannelId(), ticket);

    }

    public Ticket getTicket(long channelId) {
        return tickets.get(channelId);
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof ButtonInteractionEvent event)) return;
        switch (event.getButton().getId()) {
            case "ticket:close" -> getTicket(event.getChannelIdLong()).close(event);
            case "ticket:escalate" -> getTicket(event.getChannelIdLong()).escalate(event);
            case null -> {}
            default -> throw new IllegalStateException("Unexpected value: " + event.getButton().getId());
        }
    }
}
