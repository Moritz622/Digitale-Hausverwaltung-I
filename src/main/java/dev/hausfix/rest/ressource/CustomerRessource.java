package dev.hausfix.rest.ressource;

import dev.hausfix.sql.DatabaseConnection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import dev.hausfix.rest.objects.Customer;

import java.sql.*;

@Path("customer")
public class CustomerRessource extends Ressource {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Specify XML response
    public Customer getCustomer() {
        String query = "SELECT lastname,firstname,email,password,dateofbirth,genderid FROM customers WHERE id = 1"; // Beispielhafte SQL-Abfrage

        System.out.println("20");

        Customer customer = new Customer();


        try(Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            System.out.println("hi");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer.setName(rs.getString("firstname"));
                customer.setSurname(rs.getString("lastname"));
                customer.setEmail(rs.getString("email"));
                customer.setPassword(rs.getString("password"));
                customer.setDateOfBirth(rs.getDate("dateofbirth"));
                customer.setGenderID(Integer.parseInt(rs.getString("genderid")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return customer; // Jersey will automatically convert this to XML
    }
}
