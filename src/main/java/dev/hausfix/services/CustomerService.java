package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.interfaces.ICustomerService;

import java.util.List;

public class CustomerService implements ICustomerService {

    @Override
    public void addCustomer(Customer customer) {

    }

    @Override
    public void removeCustomer(Customer customer) {

    }

    @Override
    public List<Customer> getAllCustomers() {
        return List.of();
    }
}
