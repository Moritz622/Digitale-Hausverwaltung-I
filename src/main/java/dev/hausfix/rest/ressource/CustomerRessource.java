package dev.hausfix.rest.ressource;

import dev.hausfix.entities.Customer;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dev.hausfix.rest.schema.SchemaLoader;
import jakarta.ws.rs.core.StreamingOutput;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Path("customers")
public class CustomerRessource {

    @POST
    @Path("addcustomer")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addCustomer(String jsonString) {
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer customer = customerJSONMapper.mapCustomer(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);

        customerService.addCustomer(customer);

        return Response.status(201).entity(customerJSONMapper.mapCustomer(customer).toString()).build();
    }

    @GET
    @Path("getcustomer")
    @Produces({MediaType.APPLICATION_JSON}) // Specify XML response
    public Response getCustomer() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        Customer customer;

        customer = customerService.getAllCustomers().get(0);

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        JSONObject customerJson = customerJSONMapper.mapCustomer(customer);

        customerJson = SchemaLoader.load(customerJson, "schema/CustomerJsonSchema.json");

        return Response.status(Response.Status.OK).entity(customerJson.toString()).build();
    }
}
