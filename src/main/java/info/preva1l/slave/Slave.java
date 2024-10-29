package info.preva1l.slave;

import info.preva1l.slave.commands.Cmd;
import info.preva1l.slave.commands.PanelCommand;
import info.preva1l.slave.managers.PersistenceManager;
import info.preva1l.slave.managers.TicketManager;
import info.preva1l.slave.managers.UserManager;
import info.preva1l.slave.modals.TicketModal;
import info.preva1l.slave.models.tickets.Ticket;
import info.preva1l.slave.panels.TicketPanel;
import info.preva1l.slave.pubsub.Broker;
import info.preva1l.slave.pubsub.RedisBroker;
import info.preva1l.slave.util.BasicConfig;
import info.preva1l.slave.util.Logger;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.config.sharding.ShardingSessionConfig;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

public final class Slave {
    @Getter private static Slave instance;
    @Getter private final File dataFolder = new File(System.getProperty("user.dir"));
    @Getter private JDA jda;
    @Getter private final BasicConfig config;
    @Getter private Broker broker;

    public Slave() {
        instance = this;
        this.config = new BasicConfig(this, "config.yml");
    }

    public void start() throws Exception {
        Logger.info("Bot Starting...");
        this.jda = JDABuilder.create(
                        List.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES))
                .setActivity(Activity.of(Activity.ActivityType.WATCHING, "The Console"))
                .setToken(config.getString("token"))
                .setSessionController(ShardingSessionConfig.getDefault().getSessionController())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        jda.awaitReady();

        PersistenceManager.getInstance();
        this.broker = new RedisBroker(this);
        broker.connect();

        getJda().getUsers().forEach(user -> UserManager.getInstance().cacheDiscordUser(user));
        PersistenceManager.getInstance().getAll(Ticket.class).join().forEach(ticket -> TicketManager.getInstance().cacheTicket(ticket));

        Stream.of(
                new PanelCommand()
        ).forEach(this::registerCommand);

        Stream.of(
                new TicketPanel(),
                new TicketModal(),
                UserManager.getInstance(),
                TicketManager.getInstance()
        ).forEach(getJda()::addEventListener);

        Logger.info("Bot Started!");
    }

    public Guild getGuild() {
        return getJda().getGuildById(config.getLong("guild-id"));
    }

    public Category getTicketCategory() {
        return getGuild().getCategoryById(config.getLong("categories.tickets"));
    }

    public Role getSupportRole() {
        return getGuild().getRoleById(config.getLong("roles.support"));
    }

    public Role getDevRole() {
        return getGuild().getRoleById(config.getLong("roles.developer"));
    }

    public void registerCommand(Cmd command) {
        getJda().addEventListener(command);
        getJda().updateCommands().addCommands(command.data()).queue();
    }

    /**
     * Save a resource from the jar file to the data folder.
     *
     * @param resourcePath the path
     * @param replace replace the file
     */
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (!resourcePath.isEmpty()) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = Main.class.getResourceAsStream("/" + resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found!");
            } else {
                File outFile = new File(this.dataFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(this.dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }


                if (!replace) {
                    return;
                }

                try {
                    if (outFile.exists()) {
                        Logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException ex) {
                    Logger.severe("Could not save " + outFile.getName() + " to " + outFile, ex);
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }
}
