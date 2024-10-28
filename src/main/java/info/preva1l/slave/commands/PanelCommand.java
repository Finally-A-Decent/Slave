package info.preva1l.slave.commands;

import info.preva1l.slave.panels.TicketPanel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PanelCommand implements Cmd {

    @Override
    public CommandData data() {
        return Commands.slash("panel", "Create a panel.")
                .addOptions(new OptionData(OptionType.STRING, "panel", "The panel to create.")
                        .setRequired(true)
                        .addChoice("Ticket", "ticket"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL));
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("panel")) return;
        event.deferReply(true).queue();
        switch (event.getOption("panel").getAsString()) {
            case "ticket" -> {
                if (TicketPanel.spawn(event.getMessageChannel())) {
                    event.getHook().sendMessage("Placed Panel!").setEphemeral(true).queue();
                    return;
                }
                event.getHook().sendMessage("Failed to place panel!").setEphemeral(true).queue();
            }
            default -> event.getHook().sendMessage("Panel does not exist?").setEphemeral(true).queue();
        }
    }
}
