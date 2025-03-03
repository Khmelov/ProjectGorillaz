package com.javarush.lesson07;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Cnn {

    public static final String DB_URI_KEY = "hibernate.connection.url";
    public static final String DB_USER = "hibernate.connection.username";
    public static final String DB_PASSWORD = "hibernate.connection.password";
    public static final String DB_DRIVER = "hibernate.connection.driver_class";

    static {
        try {
            Class.forName(Configuration.getProperty(DB_DRIVER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(
                Configuration.getProperty(DB_URI_KEY),
                Configuration.getProperty(DB_USER),
                Configuration.getProperty(DB_PASSWORD)
        );
    }

}
