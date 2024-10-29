package info.preva1l.slave.persistence.handlers;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import info.preva1l.slave.Slave;
import info.preva1l.slave.persistence.Dao;
import info.preva1l.slave.persistence.DatabaseHandler;
import info.preva1l.slave.persistence.DatabaseObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.UuidRepresentation;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class MongoHandler implements DatabaseHandler {
    @Getter private boolean connected = false;
    private final Slave slave = Slave.getInstance();
    private MongoClient client;

    @Override
    public void connect() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(slave.getConfig().getString("database.uri")))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
        client = MongoClients.create(settings);
        connected = true;
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void wipeDatabase() {

    }

    /**
     * Get all the objects of type <T>
     *
     * @param clazz type to get
     * @return list of <T>
     */
    @Override
    public <T extends DatabaseObject> List<T> getAll(Class<T> clazz) {
        return List.of();
    }

    /**
     * Get an object of a certain class and id.
     *
     * @param clazz type to get.
     * @param id    the int of the object.
     * @return an optional of the object, an empty optional if the object was not found.
     */
    @Override
    public <T extends DatabaseObject> Optional<T> get(Class<T> clazz, long id) {
        return Optional.empty();
    }

    /**
     * Search for an object of a certain class and matching the name.
     *
     * @param clazz type to get.
     * @param name  the name to search for, either a username or server name.
     * @return an optional of the object, an empty optional if the object was not found.
     */
    @Override
    public <T extends DatabaseObject> Optional<T> search(Class<T> clazz, String name) {
        return Optional.empty();
    }

    /**
     * Save an object of a certain type.
     *
     * @param clazz class of the object
     * @param t     object
     */
    @Override
    public <T extends DatabaseObject> void save(Class<T> clazz, T t) {

    }

    /**
     * Update an object in the database.
     *
     * @param clazz  class of the object
     * @param t      the new object
     * @param params update params
     */
    @Override
    public <T extends DatabaseObject> void update(Class<T> clazz, T t, String[] params) {

    }

    /**
     * Delete an object of t
     *
     * @param clazz class of object
     * @param t     object
     */
    @Override
    public <T extends DatabaseObject> void delete(Class<T> clazz, T t) {

    }

    /**
     * Delete a specific object in a collection
     *
     * @param clazz class of collection
     * @param t     collection
     * @param o     object to delete
     */
    @Override
    public <T extends DatabaseObject> void deleteSpecific(Class<T> clazz, T t, Object o) {

    }

    /**
     * Registers a dao with the database handler.
     *
     * @param clazz the daos linkage
     * @param dao   the instance of the dao
     */
    @Override
    public void registerDao(Class<?> clazz, Dao<? extends DatabaseObject> dao) {

    }
}
