package dev.hausfix.rest.ressource;

import dev.hausfix.entities.Customer;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dev.hausfix.rest.schema.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

@Path("customers")
public class CustomerRessource {

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addCustomer(String jsonString) {
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer = customerJSONMapper.mapCustomer(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);

        try {
            if(customerService.addCustomer(customer)) {
                return Response.status(201, "Created").entity(SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").toString()).build();
            }
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400, "Bad Request: " + e.getMessage()).entity(null).build();
        }

        return Response.status(400, "Bad Request").entity(null).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON}) // Specify XML response
    public Response getCustomer() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        JSONObject main = new JSONObject();

        JSONArray customerJSON = new JSONArray();

        for(Customer customer: customerService.getAllCustomers()){
            customerJSON.put(SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").get("customer"));
        }

        main.put("customers", customerJSON);

        return Response.status(200, "Ok").entity(main.toString()).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.TEXT_PLAIN)
    public Response putCustomer(String jsonString){
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer = customerJSONMapper.mapCustomer(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);

        try {
            if(customerService.getCustomer(customer.getId()) != null){
                customerService.updateCustomer(customer);
            }else{
                return Response.status(404).entity("Not Found").build();
            }

            return Response.status(200).entity("Ok").build();
        } catch (NoEntityFoundException e) {
            return Response.status(404).entity("Not Found").build();
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400).entity("Bad Request | " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getCustomer(@PathParam("uuid") String uuid) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer = null;

        try {
            customer = customerService.getCustomer(UUID.fromString(uuid));

            JSONObject customerJSON = new JSONObject();

            customerJSON = (JSONObject) SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").get("customer");

            return Response.status(200, "Ok").entity(customerJSON.toString()).build();
        } catch (Exception e) {
            return Response.status(404).entity("Not Found").build();
        }
    }
}
