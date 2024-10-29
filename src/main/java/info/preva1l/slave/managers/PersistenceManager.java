package info.preva1l.slave.managers;

import info.preva1l.slave.persistence.DatabaseHandler;
import info.preva1l.slave.persistence.DatabaseObject;
import info.preva1l.slave.persistence.DatabaseType;
import info.preva1l.slave.persistence.handlers.MongoHandler;
import info.preva1l.slave.util.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PersistenceManager {
    private static PersistenceManager instance;

    private final ExecutorService threadPool;
    private final Map<DatabaseType, Class<? extends DatabaseHandler>> databaseHandlers = new HashMap<>();
    private final DatabaseHandler handler;

    private PersistenceManager() {
        Logger.info("Connecting to Database and populating caches...");
        threadPool = Executors.newVirtualThreadPerTaskExecutor();
        databaseHandlers.put(DatabaseType.MONGO, MongoHandler.class);

        this.handler = initHandler();
        Logger.info("Connected to Database and populated caches!");
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
            instance.handler.connect();
        }
        return instance;
    }

    public <T extends DatabaseObject> CompletableFuture<List<T>> getAll(Class<T> clazz) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(List.of());
        }
        return CompletableFuture.supplyAsync(() -> handler.getAll(clazz), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Optional<T>> get(Class<T> clazz, long id) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> handler.get(clazz, id), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Optional<T>> search(Class<T> clazz, String search) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> handler.search(clazz, search), threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> save(Class<T> clazz, T t) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.save(clazz, t);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> delete(Class<T> clazz, T t) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.delete(clazz, t);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> update(Class<T> clazz, T t, String[] params) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.update(clazz, t, params);
            return null;
        }, threadPool);
    }

    public <T extends DatabaseObject> CompletableFuture<Void> deleteSpecific(Class<T> clazz, T t, Object o) {
        if (!isConnected()) {
            Logger.severe("Tried to perform database action when the database is not connected!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            handler.deleteSpecific(clazz, t, o);
            return null;
        }, threadPool);
    }

    public boolean isConnected() {
        return handler.isConnected();
    }

    public void shutdown() {
        handler.destroy();
    }

    private DatabaseHandler initHandler() {
        DatabaseType type = DatabaseType.MONGO;
        Logger.info("DB Type: %s".formatted(type.getFriendlyName()));
        try {
            Class<? extends DatabaseHandler> handlerClass = databaseHandlers.get(type);
            if (handlerClass == null) {
                throw new IllegalStateException("No handler for database type %s registered!".formatted(type.getFriendlyName()));
            }
            return handlerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
