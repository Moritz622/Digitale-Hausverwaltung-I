package dev.hausfix.rest.ressource;

import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("setupDB")
public class DBRessource {

    @DELETE
    public Response setupDB(){
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        dbConnection.removeAllTables();

        dbConnection.createAllTables();

        return Response.status(200).entity("Ok").build();
    }

}
