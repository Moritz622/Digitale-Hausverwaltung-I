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
    @Produces({MediaType.APPLICATION_JSON}) // Specify XML response
    public Response getCustomer() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        JSONObject main = new JSONObject();

        JSONObject customerJSON = new JSONObject();

        for(Customer customer: customerService.getAllCustomers()){
            customerJSON.append("customer", SchemaLoader.load(customerJSONMapper.mapCustomer(customer), "schema/CustomerJsonSchema.json").get("customer"));
        }

        main.put("customers", customerJSON);

        return Response.status(Response.Status.OK).entity(main.toString()).build();
    }
}
