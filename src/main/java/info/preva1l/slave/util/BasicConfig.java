package info.preva1l.slave.util;

import info.preva1l.slave.Slave;
import lombok.Getter;
import lombok.Setter;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class BasicConfig {
    private final Slave slave;
    private final String fileName;
    private YamlConfiguration configuration;

    private File file;

    public BasicConfig(Slave slave, String fileName) {
        this.slave = slave;
        this.fileName = fileName;
        this.file = new File(System.getProperty("user.dir") + "/" + fileName);
        if (!this.file.exists()) {
            slave.saveResource(fileName, false);
        }
        this.configuration = new YamlConfiguration();
        try {
            configuration.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getBoolean(String path) {
        return (this.configuration.contains(path)) && (this.configuration.getBoolean(path));
    }

    public int getInt(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public long getLong(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getLong(path);
        }
        return 0L;
    }

    public float getFloat(String path) {
        if (this.configuration.contains(path)) {
            return (float) this.configuration.getDouble(path);
        }
        return 0.0F;
    }

    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getString(path);
        }
        return path;
    }

    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getStringList(path);
        }
        return Collections.singletonList(path);
    }

    public void load() {
        this.file = new File(slave.getDataFolder(), fileName);
        if (!this.file.exists()) {
            slave.saveResource(fileName, false);
        }
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}