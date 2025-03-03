package com.javarush.lesson08;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedStatementDemo {

    public static final String SELECT_FROM_USERS = "SELECT * FROM users WHERE id=?";

    public static void main(String[] args) throws SQLException {
        Connection connection = CnnPool.get();
        try (connection;
             PreparedStatement pst = connection.prepareStatement(SELECT_FROM_USERS)
        ) {
            pst.setLong(1, 1L);
            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                System.out.println(login + "\t" + password + "\t" + role);
            }
        }
        CnnPool.close();
    }
}
