package sk.catheaven.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    private static final Logger log = LogManager.getLogger();

    private final Properties properties;
    public static String APPLICATION_NAME = "application.name";
    public static String APPLICATION_VERSION = "application.version";

    public AppProperties() {
        properties = new Properties();
        try {
            InputStream propertiesPath = AppProperties.class.getResourceAsStream("/application.properties");
            properties.load(propertiesPath);
        } catch (Exception e) { log.error("Unable to read properties file"); }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
