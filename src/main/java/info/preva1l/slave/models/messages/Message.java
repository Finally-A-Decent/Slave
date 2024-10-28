package info.preva1l.slave.models.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class Message {
    private final String content;
    @Nullable
    private final MessageEmbed embed;
}
