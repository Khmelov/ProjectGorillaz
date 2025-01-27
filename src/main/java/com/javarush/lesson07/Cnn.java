package com.javarush.lesson07;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Cnn {

    public static final String DB_URI_KEY = "db.uri";
    public static final String DB_USER = "db.user";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_DRIVER = "db.driver";

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
