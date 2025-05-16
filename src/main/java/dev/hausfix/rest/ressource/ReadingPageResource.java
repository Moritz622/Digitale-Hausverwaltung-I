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
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
    @Path("export/xml")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response exportReadingsAsXML(String jsonString) {
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

        main.put("reading", readingJSON);

        String xml = "<readings>" + XML.toString(main) + "</readings>";

        byte[] jsonBytes = xml.getBytes(StandardCharsets.UTF_8); // 4 = pretty print

        return Response.ok(jsonBytes)
                .type("application/xml; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"export.xml\"")
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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importReadings(
            @FormDataParam("token") String tokenJson,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("file") FormDataBodyPart bodyPart) {

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        System.out.println("test " + tokenJson);

        JSONObject data = new JSONObject(tokenJson);

        System.out.println("data " + data.toString());

        User sessionUser = checkSession(data, dbConnection);

        if (sessionUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        ReadingService readingService = new ReadingService(dbConnection);

        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);


        String fileName = fileDetail.getFileName();
        String mimeType = bodyPart.getMediaType().toString();

        System.out.println("Dateiname: " + fileName);
        System.out.println("MIME-Typ: " + mimeType);

        if(mimeType.matches("application/json")){
            try {
                File file = saveInputStreamToTempFile(uploadedInputStream, fileName);

                Scanner scan = new Scanner(file);

                String jsonString = "";

                while(scan.hasNextLine()){
                    jsonString += scan.nextLine();
                }

                System.out.println("try json string");

                JSONObject jsonObject = new JSONObject(jsonString);

                System.out.println(jsonString);

                JSONArray jsonArray = jsonObject.getJSONArray("readings");

                System.out.println("test1");

                ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    System.out.println("test2 " + temp.toString());

                    Reading r = readingJSONMapper.mapReading(temp);

                    System.out.println("test3");

                    r.setUser(sessionUser);

                    readingService.addReading(r);

                    System.out.println("test4");
                }
            } catch (IOException e) {
                System.out.println("io " + e.getMessage());

                throw new RuntimeException(e);
            } catch (IncompleteDatasetException e) {
                System.out.println("incomplete " + e.getMessage());

                throw new RuntimeException(e);
            } catch (SQLException e) {
                System.out.println("sql " + e.getMessage());

                throw new RuntimeException(e);
            } catch (DuplicateEntryException e) {
                System.out.println("duplicate " + e.getMessage());

                throw new RuntimeException(e);
            }
        }else if(mimeType.matches("text/xml")){
            try {
                File file = saveInputStreamToTempFile(uploadedInputStream, fileName);

                Scanner scan = new Scanner(file);

                String xmlString = "";

                while(scan.hasNextLine()){
                    xmlString += scan.nextLine();
                }

                JSONObject jsonObject = XML.toJSONObject(xmlString);

                System.out.println(jsonObject.toString());

                JSONArray jsonArray = jsonObject.getJSONArray("readings");

                System.out.println("test1");

                ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    System.out.println("test2 " + temp.toString());

                    Reading r = readingJSONMapper.mapReading(temp);

                    System.out.println("test3");

                    r.setUser(sessionUser);

                    readingService.addReading(r);

                    System.out.println("test4");
                }
            } catch (IOException e) {
                System.out.println("io " + e.getMessage());

                throw new RuntimeException(e);
            } catch (IncompleteDatasetException e) {
                System.out.println("incomplete " + e.getMessage());

                throw new RuntimeException(e);
            } catch (SQLException e) {
                System.out.println("sql " + e.getMessage());

                throw new RuntimeException(e);
            } catch (DuplicateEntryException e) {
                System.out.println("duplicate " + e.getMessage());

                throw new RuntimeException(e);
            }
        }

        return Response.ok("Upload erfolgreich").build();
    }

    public File saveInputStreamToTempFile(InputStream inputStream, String originalFileName) throws IOException {
        // Extract file extension (optional, for correct temp file naming)
        String fileSuffix = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileSuffix = originalFileName.substring(i);
        }

        // Create temp file
        File tempFile = File.createTempFile("upload_", fileSuffix);
        tempFile.deleteOnExit(); // Optional: auto-delete on JVM exit

        // Write InputStream to File
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }
}
