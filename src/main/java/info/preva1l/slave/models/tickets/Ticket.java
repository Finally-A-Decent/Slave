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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public class Ticket implements DatabaseObject {
    private final long id;
    private final long owner;
    private final List<Long> participants = new ArrayList<>();
    private final String resource;
    private final String version;
    private final String details;
    private final Map<Long, String> messages = new HashMap<>();
    private long channelId = 0;
    @Nullable
    private WebhookData webhookData;
    private TicketStatus status;

    public CompletableFuture<TextChannel> open(@Nullable User opener) {
        if (opener == null) return CompletableFuture.completedFuture(null);
        Slave slave = Slave.getInstance();
        CompletableFuture<TextChannel> future = new CompletableFuture<>();
        slave.getGuild().createTextChannel(resource + "-" + opener.getName().substring(0, 4), slave.getTicketCategory())
                .addMemberPermissionOverride(opener.getId(), List.of(
                        Permission.VIEW_CHANNEL,
                        Permission.MESSAGE_SEND
                ), List.of()).queue(channel -> {
                    channelId = channel.getIdLong();
                    Message.builder()
                            .type(Message.Type.WEB_BOUND_TICKET_CREATE)
                            .payload(Payload.withTicketAction(new TicketAction(this.getId(), opener.getId())))
                            .build().send(Slave.getInstance().getBroker());

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("%s's Ticket".formatted(opener.getMember().getEffectiveName()));
                    eb.addField("Version", version, false);
                    eb.addField("Details", details, false);
                    eb.setFooter("If your issue is urgent (dupe/data related) and requires a development fix, click escalate below!");

                    Button closeButton = Button.of(ButtonStyle.DANGER, "ticket:close", "Close Ticket");
                    Button escalateButton = Button.of(ButtonStyle.SECONDARY, "ticket:escalate", "Escalate Ticket");

                    channel.sendMessage(new MessageCreateBuilder()
                            .setContent("%s | %s".formatted(opener.getMember().getAsMention(), slave.getSupportRole().getAsMention()))
                            .addEmbeds(eb.build())
                            .addComponents(ActionRow.of(closeButton, escalateButton)).build()).queue();
                    future.complete(channel);
                    status = TicketStatus.OPEN;
                    update();
                });
        return future;
    }

    public void close(ComponentInteraction interaction) {
        if (!status.isOpen()) {
            interaction.reply("You cannot close this ticket!").setEphemeral(true).queue();
            return;
        }
        status = TicketStatus.CLOSED;
        interaction.reply("Ticket closing soon...").queue();
        interaction.getChannel().delete().queueAfter(2, TimeUnit.SECONDS);
        update();
        Message.builder()
                .type(Message.Type.WEB_BOUND_TICKET_CLOSE)
                .payload(Payload.withTicketAction(new TicketAction(this.getId(), interaction.getUser().getIdLong())))
                .build().send(Slave.getInstance().getBroker());
    }

    public void escalate(ComponentInteraction interaction) {
        if (status != TicketStatus.OPEN) {
            interaction.reply("You cannot escalate this ticket!").setEphemeral(true).queue();
            return;
        }
        status = TicketStatus.ESCALATED;

        interaction.reply("%s %s has escalated the ticket!".formatted(Slave.getInstance().getDevRole(), interaction.getUser().getEffectiveName())).queue();
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

    public void sendMessage(MessageCreateBuilder message) {
        getChannel().sendMessage(message.build()).queue();

        // todo: figure out how to send store embeds...
//        Message.builder()
//                .type(Message.Type.WEB_BOUND_TICKET_MESSAGE)
//                .payload(Payload.withTicketMessage(new TicketMessage(this.getId(), sender.getId(), message)))
//                .build().send(Slave.getInstance().getBroker());
    }

    private TextChannel getChannel() {
        return Slave.getInstance().getGuild().getTextChannelById(channelId);
    }

    public void update() {
        TicketManager.getInstance().cacheTicket(this);
        PersistenceManager.getInstance().save(Ticket.class, this);
    }
}
