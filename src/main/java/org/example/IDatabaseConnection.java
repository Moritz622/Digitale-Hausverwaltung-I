package org.example;

import java.util.Properties;

public interface IDatabaseConnection {
    // Öffnet die Verbindung zur DB und gibt Verbindung selbst zurück
    IDatabaseConnection openConnection(Properties properties);
    void createAllTables();
    void truncateAllTables();
    void removeAllTables();
    void closeConnection();
}
