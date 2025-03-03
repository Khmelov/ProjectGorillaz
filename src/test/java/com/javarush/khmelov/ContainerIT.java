package com.javarush.khmelov;

import com.javarush.khmelov.config.ApplicationProperties;
import com.javarush.khmelov.config.Config;
import com.javarush.khmelov.config.NanoSpring;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.javarush.khmelov.config.ApplicationProperties.*;

public class ContainerIT {

    private final static JdbcDatabaseContainer<?> CONTAINER;

    public static final String DOCKER_IMAGE_NAME = "postgres:16.3";

    static {
        //create
        CONTAINER = new PostgreSQLContainer<>(DOCKER_IMAGE_NAME);
        CONTAINER.start();
        //set new properties from TestContainers
        ApplicationProperties properties = NanoSpring.find(ApplicationProperties.class);
        properties.setProperty(HIBERNATE_CONNECTION_URL, CONTAINER.getJdbcUrl());
        properties.setProperty(HIBERNATE_CONNECTION_USERNAME, CONTAINER.getUsername());
        properties.setProperty(HIBERNATE_CONNECTION_PASSWORD, CONTAINER.getPassword());
        //fill db
        Config config = NanoSpring.find(Config.class);
        config.fillEmptyRepository(); // need transactional
    }
}
