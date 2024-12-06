package dev.hausfix.util;

import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;

import java.time.LocalDate;

public class Helper {

    public Customer getCustomer(){
        Customer customer = new Customer();
        customer.setFirstName("Marco");
        customer.setLastName("Polo");
        customer.setBirthDate(LocalDate.parse("1969-04-30"));
        customer.setGender(EGender.U);

        return customer;
    }

}
