package dev.hausfix.rest.ressource;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
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
import org.checkerframework.checker.units.qual.C;
import org.glassfish.jersey.internal.inject.Custom;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Path("readings")
public class ReadingRessource {

    @DELETE
    @Path("/{uuid}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response deleteReading(@PathParam("uuid") String uuid) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        try {
            JSONObject readingJSON = new JSONObject();

            Reading reading = readingService.getReading(UUID.fromString(uuid));

            readingJSON = (JSONObject) SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading");

            readingService.removeReading(reading);

            return Response.status(200, "Ok").entity(readingJSON.toString()).build();
        } catch (NoEntityFoundException e) {
            return Response.status(404).entity("Not Found").build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addReading(String jsonString) {
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading reading = readingJSONMapper.mapReading(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        try {
            if (readingService.addReading(reading))
                return Response.status(201, "Created").entity(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading").toString()).build();
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400, "Bad Request: " + e.getMessage()).entity(null).build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Response.status(400, "Bad Request").entity(null).build();
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
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

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

    @GET
    @Path("/{uuid}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getReading(@PathParam("uuid") String uuid) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading reading = null;

        try {
            System.out.println(UUID.fromString(uuid));

            reading = readingService.getReading(UUID.fromString(uuid));

            JSONObject readingJSON = new JSONObject();

            readingJSON = (JSONObject) SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading");

            return Response.status(200, "Ok").entity(readingJSON.toString()).build();
        } catch (Exception e) {
            return Response.status(404).entity("Not Found").build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getReadingsByCriteria(
            @QueryParam("customer") String customerUuid,
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate,
            @QueryParam("kindOfMeter") String kindOfMeter) {

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        try {
            LocalDate start, end;

            try {
                // Define the expected format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Attempt to parse the date
                start = startDate != null ? LocalDate.parse(startDate, formatter): LocalDate.MIN;;
                end = endDate != null ? LocalDate.parse(endDate, formatter) : LocalDate.now();
            } catch (DateTimeParseException e) {
                return Response.status(400, "Bad Request: " + e.getMessage()).entity("Date does not match the format yyyy-MM-dd").build();
            }

            if(customerUuid == null){
                return Response.status(400, "Bad Request: ").entity("Customer UUID not given").build();
            }

            // Get readings based on the query parameters
            List<Reading> readings = readingService.getReadingsByCriteria(
                    UUID.fromString(customerUuid),
                    start,
                    end,
                    kindOfMeter != null ? EKindOfMeter.valueOf(kindOfMeter.toUpperCase()) : null);

            JSONArray readingJsonArray = new JSONArray();

            for (Reading reading : readings) {
                readingJsonArray.put(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading"));
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("readings", readingJsonArray);

            return Response.status(200).entity(SchemaLoader.load(responseJson, "schema/ReadingsJsonSchema.json").toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(400, "Bad Request: " + e.getMessage()).entity(null).build();
        }
    }
}