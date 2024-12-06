package dev.hausfix.interfaces;

import dev.hausfix.entities.Customer;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {

    boolean addCustomer(Customer customer) throws IncompleteDatasetException, DuplicateEntryException;

    void removeCustomer(Customer customer) throws NoEntityFoundException;

    void updateCustomer(Customer customer) throws NoEntityFoundException, IncompleteDatasetException, DuplicateEntryException;

    List<Customer> getAllCustomers();

    Customer getCustomer(UUID id) throws NoEntityFoundException;

}
