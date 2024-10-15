package dev.hausfix.sql;

import dev.hausfix.interfaces.IDatabaseConnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {

        private static Connection connection;

        public DatabaseConnection(){

        }

        @Override
        public IDatabaseConnection openConnection(Properties properties) {
                String dbUrl = properties.getProperty("admin.db.url");
                String dbUser = properties.getProperty("admin.db.user");
                String dbPassword = properties.getProperty("admin.db.pw");

                try {
                        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }

                return this;
        }

        @Override
        public void createAllTables() {
                try {
                        connection.prepareStatement("CREATE TABLE customers (id INT AUTO_INCREMENT PRIMARY KEY,lastname VARCHAR(100),firstname VARCHAR(100),email VARCHAR(100),password VARCHAR(100),birthdate DATE,genderid int);").executeQuery();
                        connection.prepareStatement("CREATE TABLE readings(id INT AUTO_INCREMENT PRIMARY KEY,comment VARCHAR(9999),customerid INT,dateofreading DATE,kindofmeterid INT,metercounter FLOAT,meterid INT,substitute BOOLEAN)").executeQuery();
                        connection.prepareStatement("CREATE TABLE gender(id INT AUTO_INCREMENT PRIMARY KEY, gender ENUM ('D','M','U','W'))").executeQuery();
                        connection.prepareStatement("CREATE TABLE kindOFMeter(id INT AUTO_INCREMENT PRIMARY KEY)").executeQuery();
                } catch (SQLException e) {

                }
        }

        @Override
        public void truncateAllTables() {

        }

        @Override
        public void removeAllTables() {
                try {
                        connection.prepareStatement("DROP TABLE customers").executeQuery();
                        connection.prepareStatement("DROP TABLE readings").executeQuery();
                        connection.prepareStatement("DROP TABLE gender").executeQuery();
                        connection.prepareStatement("DROP TABLE kindOFMeter").executeQuery();
                } catch (SQLException e) {

                }
        }

        @Override
        public void closeConnection() {
                try {
                        connection.close();
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
        }
}