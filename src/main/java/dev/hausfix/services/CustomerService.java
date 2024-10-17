package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.interfaces.ICustomerService;
import dev.hausfix.sql.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerService extends Service implements ICustomerService {

    public CustomerService(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public void addCustomer(Customer customer) {
        try {
            String id = customer.getId().toString();
            String lastname = customer.getLastName();
            String firstname = customer.getFirstName();
            String email = "test";
            String password = "1234";
            String birthdate = customer.getBirthDate().toString();
            EGender gender = customer.getGender();

            databaseConnection.getConnection().prepareStatement("INSERT INTO customers (id,lastname,firstname,email,password,birthdate,gender) VALUES ('" + id + "','" + lastname + "','" + firstname + "','" + email + "'," + password + ",DATE('" + birthdate + "'),'" + gender + "');").executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeCustomer(Customer customer) {

    }

    @Override
    public List<Customer> getAllCustomers() {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM customers").executeQuery();

            List<Customer> customers = new ArrayList<Customer>();

            while(resultsSet.next()){
                Customer customer = new Customer();

                customer.setId(UUID.fromString(resultsSet.getString("id")));
                customer.setLastName(resultsSet.getString("lastname"));
                customer.setFirstName(resultsSet.getString("firstname"));
                customer.setBirthDate(resultsSet.getDate("birthdate").toLocalDate());
                customer.setGender(EGender.valueOf(resultsSet.getString("gender")));

                customers.add(customer);
            }

            return customers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
