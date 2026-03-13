package se.liu.simjolucul.dopeslope;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("resources/config.properties")) {
            PROPERTIES.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDebug() {
        return Boolean.parseBoolean(PROPERTIES.getProperty("debug", "false"));
    }
}
