package dev.hausfix.services;

import dev.hausfix.sql.DatabaseConnection;

public abstract class Service {

    protected DatabaseConnection databaseConnection;

    public Service(DatabaseConnection databaseConnection){
        this.databaseConnection = databaseConnection;
    }

}
