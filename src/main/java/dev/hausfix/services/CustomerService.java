package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.interfaces.ICustomerService;
import dev.hausfix.sql.DatabaseConnection;

import java.sql.SQLException;
import java.util.List;

public class CustomerService extends Service implements ICustomerService {

    public CustomerService(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    @Override
    public void addCustomer(Customer customer) {
        try {
            String lastname = customer.getLastName();
            String firstname = customer.getFirstName();
            String email = "test";
            String password = "1234";
            String birthdate = customer.getBirthDate().toString();
            EGender gender = customer.getGender();

            databaseConnection.getConnection().prepareStatement("INSERT INTO customers (lastname,firstname,email,password,birthdate,gender) VALUES ('" + lastname + "','" + firstname + "','" + email + "'," + password + ",DATE('" + birthdate + "')," + gender + ");").executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeCustomer(Customer customer) {

    }

    @Override
    public List<Customer> getAllCustomers() {
        return List.of();
    }
}
