package info.preva1l.slave.modals;

import info.preva1l.slave.util.Logger;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

public class TicketModal implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof StringSelectInteractionEvent event) {
            create(event);
            return;
        }

        if (genericEvent instanceof ModalInteractionEvent event) {
            submit(event);
            return;
        }
    }

    private void create(StringSelectInteractionEvent event) {
        if (!event.getSelectMenu().getId().equals("ticket:create")) return;
        Modal.Builder modal = Modal.create("ticket:create:" + event.getValues().getFirst(), "Create Ticket");
        TextInput version = TextInput.create("version", "Version", TextInputStyle.SHORT).setRequiredRange(5, 10).setPlaceholder("1.5.0").build();
        TextInput details = TextInput.create("details", "Details", TextInputStyle.PARAGRAPH).setPlaceholder("java.lang.NullPointerException: Braincells is not defined.").build();
        modal.addComponents(ActionRow.of(version), ActionRow.of(details));

        event.replyModal(modal.build()).queue();
    }

    private void submit(ModalInteractionEvent event) {
        if (!event.getModalId().startsWith("ticket:create")) return;

//        Ticket.builder().id(123456789).owner(event.getUser().getIdLong())
//                .resource(event.getId().split(":")[2])
//                .version(event.getValue("version").getAsString())
//                .description(event.getValue("description").getAsString()).build().open(UserManager.getInstance().get(event.getUser().getIdLong()));

        event.reply("Your ticket has been created here: <#%s>".formatted("N/A")).setEphemeral(true).queue();
    }
}
