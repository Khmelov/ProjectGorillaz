package com.javarush.khmelov.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class ApplicationProperties extends Properties {

    public static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";
    public static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";
    public static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";
    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";

    @SneakyThrows
    public ApplicationProperties() {
        String applicationPropertiesFile = "/application.properties";
        log.info("Loading application properties from file: {}", applicationPropertiesFile);
        this.load(new FileReader(CLASSES_ROOT + applicationPropertiesFile));
        this.forEach((key, val) -> {
            String value = val.toString();
            if (value.startsWith("${")) {
                String[] valueData = value
                        .replace("${", "")
                        .replace("}", "")
                        .split(":", 2);
                if (valueData.length == 2) {
                    log.info("Default dev property '{}' to '{}'", key, valueData[0]);
                    value = valueData[1];
                }
                String envValue = System.getProperties().getProperty(valueData[0]);
                if (envValue != null) {
                    log.info("Enviroment dev property '{}' to '********'", key);
                    value = envValue;
                }
                setProperty(key.toString(), value);
                log.info("Setting property '{}'", key);
            }
        });
        try {
            String driver = this.getProperty(HIBERNATE_CONNECTION_DRIVER_CLASS);
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //any runtime
    public final static Path CLASSES_ROOT = Paths.get(URI.create(
            Objects.requireNonNull(
                    ApplicationProperties.class.getResource("/")
            ).toString()));

    //only in Tomcat (not use in tests)
    public final static Path WEB_INF = CLASSES_ROOT.getParent();
}
