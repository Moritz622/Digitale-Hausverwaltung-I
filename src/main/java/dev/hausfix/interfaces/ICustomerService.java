package dev.hausfix.interfaces;

import dev.hausfix.entities.Customer;
import dev.hausfix.exceptions.NoEntityFoundException;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {

    boolean addCustomer(Customer customer);

    void removeCustomer(Customer customer);

    void updateCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Customer getCustomer(UUID id) throws NoEntityFoundException;

}
