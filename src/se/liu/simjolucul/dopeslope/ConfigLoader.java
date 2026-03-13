package se.liu.simjolucul.dopeslope;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loads configuration properties from {@code resources/config.properties} and
 * provides access to them via static methods (only a debug mode for now).
 */
public enum ConfigLoader {
    INSTANCE;

    private final Map<String, String> properties;

    ConfigLoader() {
        this.properties = loadProperties();
    }

    private static Map<String, String> loadProperties() {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("resources/config.properties")) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        return Collections.unmodifiableMap(map);
    }

    public static boolean isDebug() {
        return Boolean.parseBoolean(INSTANCE.properties.getOrDefault("debug", "false"));
    }
}