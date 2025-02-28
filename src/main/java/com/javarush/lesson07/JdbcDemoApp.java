package com.javarush.lesson07;

import java.sql.*;

public class JdbcDemoApp {

    public static final String SELECT_FROM_USERS = "SELECT * FROM users";
    public static final String ADD_USERS = """
            INSERT INTO users (id, login, password, role)
            VALUES (DEFAULT, 'Carl2', 'admin', 'ADMIN'),
                   (DEFAULT, 'Alisa2', 'qwerty', 'USER'),
                   (DEFAULT, 'Bob2', '123', 'GUEST');
            """;

    public static void main(String[] args) throws SQLException {
        Cnn cnn = new Cnn();
        try (Connection connection = Cnn.get();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(SELECT_FROM_USERS);
            ResultSetMetaData metaData = resultSet.getMetaData();
            String format = "%15s%15s%15s%15s%n";
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                System.out.printf("%15s", columnName);
            }
            System.out.println();
            for (int i = 1; i <= columnCount; i++) {
                String columnTypeName = metaData.getColumnTypeName(i);
                System.out.printf("%15s", columnTypeName);
            }
            System.out.println("\n" + "=".repeat(15 * columnCount));
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                System.out.printf(format, id, login, password, role);
            }

            int countRows = statement.executeUpdate(ADD_USERS, Statement.RETURN_GENERATED_KEYS);
            System.out.println("added " + countRows + " rows");
            ResultSet generatedKeys = statement.getGeneratedKeys();
            System.out.print("Generated keys: ");
            while (generatedKeys.next()) {
                long id = generatedKeys.getLong("id");
                System.out.print(id + ", ");
            }
            System.out.println();

            String deleteSql = "DELETE FROM users WHERE id > 3";
            int deletedCount = statement.executeUpdate(deleteSql);
            if (deletedCount > 0) {
                System.out.println("delete users count: " + deletedCount);
            }
        }

    }
}
