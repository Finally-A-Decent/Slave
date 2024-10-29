package info.preva1l.slave.models;

import info.preva1l.slave.Slave;
import info.preva1l.slave.persistence.DatabaseObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@AllArgsConstructor
public class User implements DatabaseObject {
    private final long id;
    private final String name;
    private final String email;
    private final List<String> resources = new CopyOnWriteArrayList<>(List.of("fadah", "fadcg", "fadcs")); // preload with the free resources

    public boolean isAdmin() {
        return getMember().getRoles().stream().anyMatch(role -> role.getIdLong() == Slave.getInstance().getConfig().getLong("roles.developer"));
    }

    public boolean isSupport() {
        return getMember().getRoles().stream().anyMatch(role -> role.getIdLong() == Slave.getInstance().getConfig().getLong("roles.support"));
    }

    public Member getMember() {
        return Slave.getInstance().getGuild().getMemberById(id);
    }

    public String getProfilePicture() {
        return getMember().getAvatarUrl();
    }
}
