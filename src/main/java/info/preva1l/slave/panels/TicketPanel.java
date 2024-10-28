package info.preva1l.slave.panels;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TicketPanel implements EventListener {
    /**
     * Spawn the ticket panel
     *
     * @param channel channel to spawn it in.
     * @return true if success, else false
     */
    public static boolean spawn(MessageChannel channel) {
        if (channel == null) {
            return false;
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.CYAN)
                .setTitle("Support Tickets")
                .setDescription("Welcome to Finally a Decent help server!\nTo create a ticket click the `Open Ticket` button below!")
                .setFooter("F.A.R.T. (Fully Automatic Relief Tickets)");

        ItemComponent button = Button.of(ButtonStyle.PRIMARY, "ticket:create", "Create Ticket", Emoji.fromUnicode("U+1F3AB"));
        channel.sendMessageEmbeds(embed.build()).addActionRow(button).queue();
        return true;
    }


    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof ButtonInteractionEvent event)) return;
        if (!event.getButton().getId().equals("ticket:create")) return;
        StringSelectMenu menu = StringSelectMenu.create("ticket:create")
                .addOption("Fadah", "fadah")
                .addOption("Fadcg", "fadcg")
//                .addOption("Fadlc", "fadlc")
//                .addOption("Advanced Server Zones", "asz")
                .build();
        event.reply("Select the correct plugin below:").addComponents(ActionRow.of(menu)).setEphemeral(true).queue();
    }
}
