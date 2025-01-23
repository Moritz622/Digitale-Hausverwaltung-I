package dev.hausfix.util;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;

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

    public Reading getReadings(){
        Reading reading = new Reading();
        reading.setCustomer(getCustomer());
        reading.setSubstitute(true);
        reading.setComment("Marco ist schwul");
        reading.setDateOfReading(LocalDate.now());
        reading.setMeterCount(100);
        reading.setKindOfMeter(EKindOfMeter.HEIZUNG);
        reading.setMeterId("test");

        return reading;
    }

}
