package dev.hausfix.sql;

import dev.hausfix.interfaces.IDatabaseConnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {

        private Connection connection;

        public DatabaseConnection(){

        }

        public Connection getConnection(){
                return connection;
        }

        @Override
        public IDatabaseConnection openConnection(Properties properties) {
                String dbUrl = properties.getProperty("admin.db.url");
                String dbUser = properties.getProperty("admin.db.user");
                String dbPassword = properties.getProperty("admin.db.pw");
                String dbName = properties.getProperty("admin.db.name");

                try {
                        connection = DriverManager.getConnection(dbUrl + dbName, dbUser, dbPassword);
                } catch (SQLException e) {
                    try {
                        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

                        Statement stmt = connection.createStatement();
                        stmt.executeQuery("CREATE DATABASE hausfix");

                        connection = DriverManager.getConnection(dbUrl + dbName, dbUser, dbPassword);
                    } catch (SQLException ex) {
                        System.out.println("Es konnte keine Verbingund zum SQL Server aufgebaut werden.");
                    }
                }

                return this;
        }

        @Override
        public void createAllTables() {
                try {
                        connection.prepareStatement("CREATE TABLE customers (id UUID PRIMARY KEY,lastname VARCHAR(100),firstname VARCHAR(100),email VARCHAR(100),password VARCHAR(100),birthdate DATE,gender ENUM ('D','M','U','W'));").executeQuery();
                        connection.prepareStatement("CREATE TABLE readings(id UUID PRIMARY KEY,comment VARCHAR(9999),customerId VARCHAR(256),dateOfReading DATE,kindOfMeter ENUM('Heizung','Strom','Wasser','Unbekannt'),meterCount DOUBLE,meterId VARCHAR(256),substitute BOOLEAN)").executeQuery();
                        connection.prepareStatement("CREATE TABLE gender(id INT AUTO_INCREMENT PRIMARY KEY, gender ENUM ('D','M','U','W'))").executeQuery();
                        connection.prepareStatement("CREATE TABLE kindOFMeter(id INT AUTO_INCREMENT PRIMARY KEY)").executeQuery();
                } catch (SQLException e) {
                        System.out.println("Fehler bei der erstellung der Tabellen");
                }
        }

        @Override
        public void truncateAllTables() {
                try {
                        connection.prepareStatement("TRUNCATE TABLE customers").executeQuery();
                        connection.prepareStatement("TRUNCATE TABLE readings").executeQuery();
                        connection.prepareStatement("TRUNCATE TABLE gender").executeQuery();
                        connection.prepareStatement("TRUNCATE TABLE kindOFMeter").executeQuery();
                } catch (SQLException e) {

                }
        }

        @Override
        public void removeAllTables() {
                try {
                        connection.prepareStatement("DROP TABLE customers").executeQuery();
                        connection.prepareStatement("DROP TABLE readings").executeQuery();
                        connection.prepareStatement("DROP TABLE gender").executeQuery();
                        connection.prepareStatement("DROP TABLE kindOFMeter").executeQuery();
                } catch (SQLException e) {
                        System.out.println("Fehler bei der löschung der Tabellen");
                }
        }

        @Override
        public void closeConnection() {
                try {
                        connection.close();
                } catch (SQLException e) {
                        System.out.println("Verbindung zur Datenbank konnte nicht geschlossen werden.");
                }
        }
}