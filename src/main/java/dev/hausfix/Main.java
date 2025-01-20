package dev.hausfix;

import dev.hausfix.rest.Server;

public class Main {
    public static void main(String[] args) {
        Server.startServer("http://localhost:8069/rest");
    }
}