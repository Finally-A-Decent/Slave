package info.preva1l.slave.managers;

import info.preva1l.slave.models.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserManager implements EventListener {
    private static UserManager instance;

    private final Map<Long, User> usersCache = new ConcurrentHashMap<>();

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public @Nullable User getUser(long userId) {
        return usersCache.get(userId);
    }

    public void cacheDiscordUser(net.dv8tion.jda.api.entities.User discordUser) {
        Optional<User> dbUser = PersistenceManager.getInstance().get(User.class, discordUser.getIdLong()).join();
        if (dbUser.isPresent()) {
            cacheUser(dbUser.get());
            return;
        }

        cacheUser(new User(discordUser.getIdLong(), discordUser.getName(), ""));
    }

    public void cacheUser(User user) {
        usersCache.put(user.getId(), user);
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof GuildMemberJoinEvent event)) return;
        if (usersCache.containsKey(event.getUser().getIdLong())) return;
        cacheDiscordUser(event.getUser());
    }
}
