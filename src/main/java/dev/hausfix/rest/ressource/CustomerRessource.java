package dev.hausfix.rest.ressource;

import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.UUID;

@Path("customer")
public class CustomerRessource extends Ressource {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Specify XML response
    public Response getCustomer() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        Customer customer = new Customer();
        customer.setFirstName("Nina");
        customer.setLastName("Hit");
        customer.setBirthDate(LocalDate.parse("11.9.2002"));
        customer.setGender(EGender.W);

        return Response.status(Response.Status.OK).entity(customer).build();
    }
}
