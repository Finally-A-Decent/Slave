package info.preva1l.slave.persistence;

import java.io.Closeable;

/**
 * An interface for interacting with a database via passing DAOs.
 * Inherits the methods and functionality from {@link DataHandler}.
 */
public interface DatabaseHandler extends DataHandler {
    boolean isConnected();
    void connect();
    void destroy();
    void wipeDatabase();
}