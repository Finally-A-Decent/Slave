package info.preva1l.slave.commands;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface Cmd extends EventListener {
    CommandData data();

    void onCommand(SlashCommandInteractionEvent event);

    @Override
    default void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof SlashCommandInteractionEvent event) onCommand(event);
    }
}
