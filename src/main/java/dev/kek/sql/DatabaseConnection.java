package dev.kek.sql;

import java.sql.*;

public class DatabaseConnection {
        private static final String URL = "jdbc:mariadb://localhost:3306/hausfix"; // Deine DB-URL
        private static final String USER = "root"; // Dein DB-Benutzername
        private static final String PASSWORD = "12345678"; // Dein DB-Passwort

        private static Connection connection;

        public static Connection getConnection() {
                if (connection == null) {
                        try {
                                System.out.println("akk");
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                System.out.println("Connection established successfully!");
                        } catch (SQLException e) {
                                System.err.println("Failed to establish connection: " + e.getMessage());
                                e.printStackTrace();
                        }
                }
                return connection;
        }

}
