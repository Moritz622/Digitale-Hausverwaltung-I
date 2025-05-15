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
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

}
