package dev.hausfix.interfaces;

import dev.hausfix.entities.Customer;

import java.util.List;

public interface ICustomerService {

    void addCustomer(Customer customer);

    void removeCustomer(Customer customer);

    List<Customer> getAllCustomers();

}
