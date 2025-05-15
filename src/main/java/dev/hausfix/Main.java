package dev.hausfix;

import dev.hausfix.rest.Server;
import dev.hausfix.sql.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        Server.startServer("http://localhost:8069/");

    }
}