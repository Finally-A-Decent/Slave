package info.preva1l.slave.pubsub;


import com.google.gson.Gson;
import info.preva1l.slave.Slave;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class Broker {
    protected final Slave slave;
    protected final Gson gson;

    protected Broker(@NotNull Slave slave) {
        this.slave = slave;
        this.gson = new Gson();
    }

    protected void handle(@NotNull Message message) {
        switch (message.getType()) {

            default -> throw new IllegalStateException("Unexpected value: " + message.getType());
        }
    }

    public abstract void connect();

    protected abstract void send(@NotNull Message message);

    public abstract void destroy();

    @Getter
    @AllArgsConstructor
    public enum Type {
        REDIS("Redis"),
        ;
        private final String displayName;
    }
}
