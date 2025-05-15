package dev.hausfix.rest.ressource;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.entities.User;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.rest.objects.ReadingJSONMapper;
import dev.hausfix.rest.schema.SchemaLoader;
import dev.hausfix.rest.webtoken.JWTUtil;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.services.UserService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("rest/readingpage")
public class ReadingPageResource {

    private User checkSession(JSONObject data, DatabaseConnection dbConnection){
        if(data.has("token")){
            UUID sessionUserToken = JWTUtil.validateToken(data.getString("token"));

            if(sessionUserToken != null){
                UserService userService = new UserService(dbConnection);

                try {
                    return userService.getUser(sessionUserToken);
                } catch (NoEntityFoundException e) {
                    return null;
                }
            }
        }

        return null;
    }

    @POST
    @Path("getreading")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getReading(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        System.out.println(data);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        String id = data.getString("readingid");

        System.out.println(id);

        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading reading;

        try {
            reading = readingService.getReading(UUID.fromString(id));

            if(((User) reading.getUser()).getId().toString().matches(sessionUser.getId().toString())){
                return Response.status(Response.Status.OK).entity(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").toString()).build();
            }else{
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No Entity Found").build();
        }
    }

    @POST
    @Path("getallreadings")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllReadings(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        ArrayList<Reading> readings = readingService.getAllReadings();

        ArrayList<Reading> userReadings = new ArrayList<>();

        for(Reading reading : readings){
            if(reading.getUser() != null){
                if(((User) reading.getUser()).getId().toString().matches(sessionUser.getId().toString())){
                    userReadings.add(reading);
                }
            }
        }

        JSONObject main = new JSONObject();

        JSONArray readingJSON = new JSONArray();

        for(Reading reading : userReadings){
            readingJSON.put(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading"));
        }

        main.put("readings", readingJSON);

        return Response.status(Response.Status.OK).entity(SchemaLoader.load(main, "schema/ReadingsJsonSchema.json").toString()).build();
    }

    @POST
    @Path("addreading")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addReading(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        CustomerService customerService = new CustomerService(dbConnection);
        ReadingService readingService = new ReadingService(dbConnection);
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        customerService.setReadingService(readingService);
        readingService.setCustomerService(customerService);

        Reading reading = readingJSONMapper.mapReading(data.getJSONObject("reading"));

        reading.setUser(sessionUser);

        Customer temp = (Customer) reading.getCustomer();

        if(!((User)temp.getUser()).getId().equals(sessionUser.getId())){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        try {
            readingService.addReading(reading);

            return Response.status(Response.Status.OK).entity(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").toString()).build();
        } catch (IncompleteDatasetException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Incomplete dataset").build();
        } catch (DuplicateEntryException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cant use that name").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("removereading")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response removeReading(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        UserService userService = new UserService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);
        ReadingService readingService = new ReadingService(dbConnection);
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        customerService.setReadingService(readingService);
        readingService.setCustomerService(customerService);

        Reading reading = null;

        try {
            reading = readingService.getReading(UUID.fromString(data.get("readingid").toString()));

            if(((User)reading.getCustomer().getUser()).getId().equals(sessionUser.getId())){
                readingService.removeReading(reading);
            }else{
                return Response.status(Response.Status.UNAUTHORIZED).entity("Not your Reading").build();
            }

            return Response.status(Response.Status.OK).build();
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Reading does not exists").build();
        }
    }

    @POST
    @Path("putreading")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response putReading(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        ReadingService readingService = new ReadingService(dbConnection);

        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading reading = readingJSONMapper.mapReading(data.getJSONObject("reading"));

        try {
            Reading temp = readingService.getReading(reading.getId());

            if(!((User)temp.getUser()).getId().equals(sessionUser.getId())){
                return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
            }
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Reading doesn't exist").build();
        }

        try {
            readingService.updateReading(reading);

            return Response.status(Response.Status.OK).build();
        } catch (NoEntityFoundException e) {

            return Response.status(Response.Status.BAD_REQUEST).entity("Reading doesn't exist").build();
        }
    }

    @POST
    @Path("export/json")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response exportReadingsAsJSON(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if (sessionUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        ReadingService readingService = new ReadingService(dbConnection);

        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ArrayList<Reading> readings = readingService.getAllReadings();

        ArrayList<Reading> userReadings = new ArrayList<>();

        for (Reading reading : readings) {
            if (reading.getUser() != null) {
                if (((User) reading.getUser()).getId().toString().matches(sessionUser.getId().toString())) {
                    userReadings.add(reading);
                }
            }
        }

        JSONObject main = new JSONObject();

        JSONArray readingJSON = new JSONArray();

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        for(Reading reading : userReadings){
            readingJSON.put(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading"));
        }

        main.put("readings", readingJSON);

        byte[] jsonBytes = main.toString(4).getBytes(StandardCharsets.UTF_8); // 4 = pretty print

        return Response.ok(jsonBytes)
                .type("application/json; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"export.json\"")
                .build();
    }

    @POST
    @Path("export/csv")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response exportReadingsAsCSV(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if (sessionUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        ReadingService readingService = new ReadingService(dbConnection);

        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        ArrayList<Reading> readings = readingService.getAllReadings();

        ArrayList<Reading> userReadings = new ArrayList<>();

        for(Reading reading : readings){
            if(reading.getUser() != null){
                if(((User) reading.getUser()).getId().toString().matches(sessionUser.getId().toString())){
                    userReadings.add(reading);
                }
            }
        }

        File file = null;

        try {
            file = File.createTempFile("readings", ".csv");

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

            pw.println("Wert,Typ,Datum,Kommentar,Substitute,Meter ID,Kunde Vorname,Kunde Nachname");

            for(Reading userReading : userReadings){
                pw.println(userReading.getMeterCount() + "," + userReading.getKindOfMeter() + "," + userReading.getDateOfReading() + "," + userReading.getComment() + "," + userReading.getSubstitute() + "," + userReading.getMeterId() + "," + userReading.getCustomer().getFirstName() + "," + userReading.getCustomer().getLastName());
            }

            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Response.ok(file, MediaType.valueOf("text/csv; charset=UTF-8"))
                .header("Content-Disposition", "attachment; filename=\"export.csv\"")
                .build();
    }

    @POST
    @Path("importreadings")
    @Consumes("text/csv")
    public Response importReadingsFromCSV(InputStream uploadedInputStream) {
        try {
            List<Reading> readings = parseCSV(uploadedInputStream);
            return Response.ok("CSV erfolgreich verarbeitet").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Fehler beim Verarbeiten der CSV: " + e.getMessage())
                    .build();
        }
    }

    public List<Reading> parseCSV(InputStream inputStream) throws IOException {
        List<Reading> readings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length < 6) continue;

                double value = Double.parseDouble(parts[0]);
                String type = parts[1];
                String date = parts[2];
                String comment = parts[3];
                String substitute = parts[4];
                String deviceId = parts[5];

                Reading reading = new Reading();
                reading.setMeterCount(value);
                reading.setKindOfMeter(EKindOfMeter.valueOf(type));

                readings.add(reading);
            }
        }

        return readings;
    }
}
