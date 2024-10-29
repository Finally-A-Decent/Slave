package info.preva1l.slave.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.logging.*;

@UtilityClass
public class Logger {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Slave");

    private final Formatter formatter = new SimpleFormatter() {
        @Override
        public synchronized String format(LogRecord lr) {
            return String.format("[%1$tT %2$s]: [%3$s] %4$s%n",
                    new Date(lr.getMillis()),
                    lr.getLevel().getLocalizedName(),
                    lr.getLoggerName(),
                    lr.getMessage());
        }
    };

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        java.util.logging.Logger.getGlobal().setUseParentHandlers(false);
        java.util.logging.Logger.getGlobal().addHandler(consoleHandler);
        java.util.logging.Logger.getLogger("").setUseParentHandlers(false);
        java.util.logging.Logger.getLogger("").addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
    }

    public void info(@NotNull String message) {
        logger.log(Level.INFO, message);
    }

    public void warn(@NotNull String message) {
        logger.log(Level.WARNING, message);
    }

    public void warn(@NotNull String message, @NotNull Throwable e) {
        logger.log(Level.WARNING, message, e);
    }

    public void severe(@NotNull String message) {
        logger.severe(message);
    }

    public void severe(@NotNull String message, @NotNull Exception e) {
        logger.log(Level.SEVERE, message, e);
    }

    public void debug(@NotNull String message) {
        logger.log(Level.FINE, message);
    }
}
