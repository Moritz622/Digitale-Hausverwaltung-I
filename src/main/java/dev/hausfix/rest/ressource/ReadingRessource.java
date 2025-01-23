package dev.hausfix.rest.ressource;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.rest.objects.ReadingJSONMapper;
import dev.hausfix.rest.schema.SchemaLoader;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.Helper;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;

@Path("readings")
public class ReadingRessource {

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addReading(String jsonString) {
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading reading = readingJSONMapper.mapReading(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        ReadingService readingService = new ReadingService(dbConnection);

        try {
            if (readingService.addReading(reading))
                return Response.status(201, "Created").entity(readingJSONMapper.mapReading(reading).toString()).build();
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400, "Bad Request: " + e.getMessage()).entity(null).build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Response.status(400, "Bad Request").entity(null).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON}) // Specify XML response
    public Response getReading() throws IncompleteDatasetException, SQLException, DuplicateEntryException {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        ReadingService readingService = new ReadingService(dbConnection);

        Helper helper = new Helper();

        readingService.addReading(helper.getReadings());

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        JSONObject main = new JSONObject();

        JSONArray readingJson = new JSONArray();

        for (Reading reading : readingService.getAllReadings()) {
            readingJson.put(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading"));
        }

        main.put("readings", readingJson);

        return Response.status(200, "Ok").entity(main.toString()).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.TEXT_PLAIN)
    public Response putReading(String jsonString) {
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading reading = readingJSONMapper.mapReading(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        ReadingService readingService = new ReadingService(dbConnection);

        try {
            if (readingService.getReading(reading.getId()) != null) {
                readingService.updateReading(reading);
            } else {
                return Response.status(404).entity("Not Found").build();
            }

            return Response.status(200).entity("Ok").build();
        } catch (NoEntityFoundException e) {
            return Response.status(404).entity("Not Found").build();
        }
    }
}