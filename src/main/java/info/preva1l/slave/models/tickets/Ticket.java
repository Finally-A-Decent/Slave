package info.preva1l.slave.models.tickets;

import info.preva1l.slave.Slave;
import info.preva1l.slave.managers.PersistenceManager;
import info.preva1l.slave.managers.TicketManager;
import info.preva1l.slave.models.User;
import info.preva1l.slave.models.WebhookData;
import info.preva1l.slave.persistence.DatabaseObject;
import info.preva1l.slave.pubsub.Message;
import info.preva1l.slave.pubsub.Payload;
import info.preva1l.slave.pubsub.messages.TicketAction;
import info.preva1l.slave.pubsub.messages.TicketMessage;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder(builderClassName = "Builder")
public class Ticket implements DatabaseObject {
    private final long id;
    private final long owner;
    private final List<Long> participants;
    private final String resource;
    private final String version;
    private final String description;
    private final Map<Long, String> messages = new HashMap<>();
    private long channelId;
    private WebhookData webhookData;
    private TicketStatus status;

    public void open(User opener) {
        Slave slave = Slave.getInstance();
        TextChannel channel = slave.getGuild().createTextChannel(resource + "-" + opener.getName().substring(0, 4), slave.getTicketCategory()).complete();
        channelId = channel.getIdLong();

        Message.builder()
                .type(Message.Type.WEB_BOUND_TICKET_CREATE)
                .payload(Payload.withTicketAction(new TicketAction(this.getId(), opener.getId())))
                .build().send(Slave.getInstance().getBroker());
    }

    public void close(ComponentInteraction interaction) {
        if (status != TicketStatus.OPEN) {
            interaction.reply("").setEphemeral(true).queue();
            return;
        }
        status = TicketStatus.CLOSED;
        update();

        Message.builder()
                .type(Message.Type.WEB_BOUND_TICKET_CLOSE)
                .payload(Payload.withTicketAction(new TicketAction(this.getId(), interaction.getUser().getIdLong())))
                .build().send(Slave.getInstance().getBroker());
    }

    /**
     * Send a message from the web client.
     *
     * @param sender  the user who sent the message
     * @param message the message
     */
    public void sendMessage(User sender, String message) {
        messages.put(sender.getId(), message);
        update();

        Message.builder()
                .type(Message.Type.WEB_BOUND_TICKET_MESSAGE)
                .payload(Payload.withTicketMessage(new TicketMessage(this.getId(), sender.getId(), message)))
                .build().send(Slave.getInstance().getBroker());
    }

    private TextChannel getChannel() {
        return Slave.getInstance().getGuild().getTextChannelById(channelId);
    }

    public void update() {
        TicketManager.getInstance().cacheTicket(this);
        PersistenceManager.getInstance().save(Ticket.class, this);
    }
}
