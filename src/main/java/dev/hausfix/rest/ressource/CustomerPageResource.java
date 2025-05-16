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
import dev.hausfix.util.Helper;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.*;
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

@Path("rest/customerpage")
public class CustomerPageResource {

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
    @Path("getcustomer")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getCustomer(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        String id = data.getString("customerid");

        CustomerService customerService = new CustomerService(dbConnection);
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer;

        try {
            customer = customerService.getCustomer(UUID.fromString(id));

            if(((User) customer.getUser()).getId().toString().matches(sessionUser.getId().toString())){
                return Response.status(Response.Status.OK).entity(SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").toString()).build();
            }else{
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No Entity Found").build();
        }
    }

    @POST
    @Path("getallcustomers")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllCustomers(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        CustomerService customerService = new CustomerService(dbConnection);
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        ArrayList<Customer> customers = customerService.getAllCustomers();

        ArrayList<Customer> userCustomers = new ArrayList<>();

        for(Customer customer : customers){
            if(customer.getUser() != null){
                if(((User) customer.getUser()).getId().toString().matches(sessionUser.getId().toString())){
                    userCustomers.add(customer);
                }
            }
        }

        JSONObject main = new JSONObject();

        JSONArray customerJSON = new JSONArray();

        for(Customer customer : userCustomers){
            customerJSON.put(SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").get("customer"));
        }

        main.put("customers", customerJSON);

        return Response.status(Response.Status.OK).entity(SchemaLoader.load(main, "schema/CustomersJsonSchema.json").toString()).build();
    }

    @POST
    @Path("addcustomer")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addCustomer(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        CustomerService customerService = new CustomerService(dbConnection);
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer = customerJSONMapper.mapCustomer(data.getJSONObject("customer"));

        customer.setUser(sessionUser);

        try {
            if(customerService.addCustomer(customer)){
                return Response.status(Response.Status.OK).entity(customerJSONMapper.mapCustomer(customerService.getCustomer(customer.getId())).toString()).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not add Customer").build();
            }
        } catch (IncompleteDatasetException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Incomplete dataset").build();
        } catch (DuplicateEntryException e) {
            System.out.println(e.getMessage());

            return Response.status(Response.Status.CONFLICT).entity("Customer already exists").build();
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not add Customer").build();
        }
    }

    @POST
    @Path("removecustomer")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response removeCustomer(String jsonString) {
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
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        customerService.setReadingService(readingService);
        readingService.setCustomerService(customerService);

        Customer customer = null;

        try {
            customer = customerService.getCustomer(UUID.fromString(data.get("customerid").toString()));

            if(((User)customer.getUser()).getId().equals(sessionUser.getId())){
                customerService.removeCustomer(customer);
            }else{
                return Response.status(Response.Status.UNAUTHORIZED).entity("Not your Customer").build();
            }

            return Response.status(Response.Status.OK).build();
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer does not exists").build();
        }
    }

    @POST
    @Path("putcustomer")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response putCustomer(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        CustomerService customerService = new CustomerService(dbConnection);
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer = customerJSONMapper.mapCustomer(data.getJSONObject("customer"));

        customer.setUser(sessionUser);

        try {
            Customer temp = customerService.getCustomer(customer.getId());

            if(!((User)temp.getUser()).getId().equals(sessionUser.getId())){
                System.out.println("Your session has expired");

                return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
            }
        } catch (NoEntityFoundException e) {
            System.out.println("Customer doesn't exist");

            return Response.status(Response.Status.BAD_REQUEST).entity("Customer doesn't exist").build();
        }

        try {
            customerService.updateCustomer(customer);

            return Response.status(Response.Status.OK).build();
        } catch (NoEntityFoundException e) {
            System.out.println("Customer doesn't exist");

            return Response.status(Response.Status.BAD_REQUEST).entity("Customer doesn't exist").build();
        } catch (IncompleteDatasetException e) {
            System.out.println("Incomplete dataset");

            return Response.status(Response.Status.BAD_REQUEST).entity("Incomplete dataset").build();
        } catch (DuplicateEntryException e) {
            System.out.println("Cant use that name");

            return Response.status(Response.Status.BAD_REQUEST).entity("Cant use that name").build();
        }
    }

    @POST
    @Path("getcustomerreadings")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getCustomerReadings(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        CustomerService customerService = new CustomerService(dbConnection);
        ReadingService readingService = new ReadingService(dbConnection);
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        customerService.setReadingService(readingService);
        readingService.setCustomerService(customerService);

        System.out.println("test1");

        try {
            Customer temp = customerService.getCustomer(UUID.fromString(data.get("customerid").toString()));

            if(!((User)temp.getUser()).getId().equals(sessionUser.getId())){
                return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
            }
        } catch (NoEntityFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer doesn't exist").build();
        }

        System.out.println("test2");

        try {
            List<Reading> readings;

            System.out.println("huan: " + jsonString);

            if(!data.getString("type").matches("")){
                readings = readingService.getReadingsByCriteria(UUID.fromString(data.getString("customerid")), LocalDate.parse(data.getString("startdate")), LocalDate.parse(data.getString("enddate")), EKindOfMeter.valueOf(data.getString("type")));
            }else{
                readings = readingService.getReadingsByCriteria(UUID.fromString(data.getString("customerid")), LocalDate.parse(data.getString("startdate")), LocalDate.parse(data.getString("enddate")), null);
            }

            System.out.println("test3");

            JSONObject main = new JSONObject();

            JSONArray readingJSON = new JSONArray();

            ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

            System.out.println("test4");

            for(Reading reading : readings) {
                readingJSON.put(SchemaLoader.load(readingJSONMapper.mapReading(reading), "schema/ReadingJsonSchema.json").get("reading"));
            }

            System.out.println("test5");

            main.put("readings", readingJSON);

            System.out.println("test6");

            return Response.status(Response.Status.OK).entity(SchemaLoader.load(main, "schema/ReadingsJsonSchema.json").toString()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("changepassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject data = new JSONObject(jsonString);

        User sessionUser = checkSession(data, dbConnection);

        if(sessionUser == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Your session has expired").build();
        }

        UserService userService = new UserService(dbConnection);

        String newPassword = data.getString("password");

        sessionUser.setPassword(newPassword);

        try {
            userService.updateUser(sessionUser);

            return Response.status(Response.Status.OK).build();
        } catch (NoEntityFoundException | DuplicateEntryException | IncompleteDatasetException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("export/json")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response exportCustomersAsJSON(String jsonString) {
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

        ArrayList<Customer> customers = customerService.getAllCustomers();

        ArrayList<Customer> userCustomers = new ArrayList<>();

        for (Customer customer : customers) {
            if (customer.getUser() != null) {
                if (((User) customer.getUser()).getId().toString().matches(sessionUser.getId().toString())) {
                    userCustomers.add(customer);
                }
            }
        }

        JSONObject main = new JSONObject();

        JSONArray customerJSON = new JSONArray();

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        for(Customer customer : userCustomers){
            customerJSON.put(SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").get("customer"));
        }

        main.put("customers", customerJSON);

        byte[] jsonBytes = main.toString(4).getBytes(StandardCharsets.UTF_8); // 4 = pretty print

        return Response.ok(jsonBytes)
                .type("application/json; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"export.json\"")
                .build();
    }

    @POST
    @Path("export/xml")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response exportCustomersAsXML(String jsonString) {
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

        ArrayList<Customer> customers = customerService.getAllCustomers();

        ArrayList<Customer> userCustomers = new ArrayList<>();

        for (Customer customer : customers) {
            if (customer.getUser() != null) {
                if (((User) customer.getUser()).getId().toString().matches(sessionUser.getId().toString())) {
                    userCustomers.add(customer);
                }
            }
        }

        JSONObject main = new JSONObject();

        JSONArray customerJSON = new JSONArray();

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        for(Customer customer : userCustomers){
            customerJSON.put(SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").get("customer"));
        }

        main.put("customer", customerJSON);

        String xml = "<customers>" + XML.toString(main) + "</customers>";

        byte[] jsonBytes = xml.getBytes(StandardCharsets.UTF_8); // 4 = pretty print

        return Response.ok(jsonBytes)
                .type("application/xml; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"export.xml\"")
                .build();
    }

    @POST
    @Path("export/csv")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response exportCustomersAsCSV(String jsonString) {
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

        ArrayList<Customer> customers = customerService.getAllCustomers();

        ArrayList<Customer> userCustomers = new ArrayList<>();

        for(Customer customer : customers){
            if(customer.getUser() != null){
                if(((User) customer.getUser()).getId().toString().matches(sessionUser.getId().toString())){
                    userCustomers.add(customer);
                }
            }
        }

        File file = null;

        try {
            file = File.createTempFile("customers", ".csv");

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));

            pw.println("Nachname,Vorname,Geburtsdatum,Geschlecht");

            for(Customer userCustomer : userCustomers){
                pw.println(userCustomer.getLastName() + "," + userCustomer.getFirstName() + "," + userCustomer.getBirthDate() + "," + userCustomer.getGender());
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

                JSONArray jsonArray = jsonObject.getJSONArray("customers");

                System.out.println("test1");

                CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    System.out.println("test2 " + temp.toString());

                    Customer r = customerJSONMapper.mapCustomer(temp);

                    System.out.println("test3");

                    r.setUser(sessionUser);
                    r.setId(UUID.randomUUID());

                    customerService.addCustomer(r);

                    System.out.println("test4");
                }
            } catch (IOException e) {
                System.out.println("io " + e.getMessage());

                throw new RuntimeException(e);
            } catch (IncompleteDatasetException e) {
                System.out.println("incomplete " + e.getMessage());

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


                JSONObject customers = jsonObject.getJSONObject("customers");

                Object readingObj = customers.get("customer");

                JSONArray customerArray;

                if (readingObj instanceof JSONArray) {
                    customerArray = (JSONArray) readingObj;
                } else {
                    customerArray = new JSONArray();
                    customerArray.put(readingObj);
                }

                System.out.println(jsonObject.toString());

                JSONArray jsonArray = customerArray;

                System.out.println("test1");

                CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject temp = jsonArray.getJSONObject(i);

                    System.out.println("test2 " + temp.toString());

                    Customer r = customerJSONMapper.mapCustomer(temp);

                    System.out.println("test3");

                    r.setUser(sessionUser);
                    r.setId(UUID.randomUUID());

                    customerService.addCustomer(r);

                    System.out.println("test4");
                }
            } catch (IOException e) {
                System.out.println("io " + e.getMessage());

                throw new RuntimeException(e);
            } catch (IncompleteDatasetException e) {
                System.out.println("incomplete " + e.getMessage());

                throw new RuntimeException(e);
            }catch (DuplicateEntryException e) {
                System.out.println("duplicate " + e.getMessage());

                throw new RuntimeException(e);
            }
        }else if(mimeType.matches("text/csv")){
            try {
                File file = saveInputStreamToTempFile(uploadedInputStream, fileName);

                Scanner scan = new Scanner(file);

                ArrayList<Reading> importReadingsList = new ArrayList<Reading>();

                scan.nextLine();

                //Wert,Typ,Datum,Kommentar,Substitute,MessgerätID,Kunde Vorname,Kunde Nachname

                ArrayList<Customer> customers = customerService.getAllCustomers();
                ArrayList<Customer> userCustomers = new ArrayList<Customer>();

                for(Customer c : customers){
                    if(((User)c.getUser()).getId() == sessionUser.getId()){
                        userCustomers.add(c);
                    }
                }

                while(scan.hasNextLine()) {
                    String[] readingData = scan.nextLine().split(",");

                    Reading tempReading = new Reading();
                    tempReading.setMeterCount(Double.parseDouble(readingData[0]));
                    tempReading.setKindOfMeter(EKindOfMeter.valueOf(readingData[1]));
                    tempReading.setDateOfReading(LocalDate.parse(readingData[2]));
                    tempReading.setComment(readingData[3]);
                    tempReading.setSubstitute(Boolean.parseBoolean(readingData[4]));
                    tempReading.setMeterId(readingData[5]);

                    String vorname = readingData[6];
                    String nachname = readingData[7];

                    Customer huan = null;

                    for (Customer c : userCustomers) {
                        if (c.getFirstName().matches(vorname) & c.getLastName().matches(nachname)) {
                            huan = c;

                            break;
                        }
                    }

                    if(huan != null){
                        tempReading.setCustomer(huan);
                    }else{
                        tempReading.setCustomer(null);
                    }

                    readingService.addReading(tempReading);

                    System.out.println("kek");
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IncompleteDatasetException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (DuplicateEntryException e) {
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
