package dev.hausfix.util;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class Helper {

    public final UUID customerUUID = UUID.fromString("13b53ae0-15c8-4273-80ac-2587699485b6");
    public final UUID readingUUID = UUID.fromString("478d3b24-0ba0-4901-b8cc-9002b9eb0da6");

    public Customer customer;

    public Reading reading;

    public Helper(){
        customer = new Customer();
        customer.setFirstName("Marco");
        customer.setLastName("Polo");
        customer.setBirthDate(LocalDate.parse("1969-04-30"));
        customer.setGender(EGender.U);
        customer.setId(customerUUID);
        customer.setUser(null);

        reading = new Reading();
        reading.setCustomer(getCustomer());
        reading.setSubstitute(true);
        reading.setComment("Test Reading");
        reading.setDateOfReading(LocalDate.now());
        reading.setMeterCount(100);
        reading.setKindOfMeter(EKindOfMeter.HEIZUNG);
        reading.setMeterId("69");
        reading.setId(readingUUID);
    }

    public Customer getRandomCustomer(){
        Random random = new Random();

        Customer customer = new Customer();
        customer.setFirstName("Marco " + random.nextInt(1000000000));
        customer.setLastName("Polo " + random.nextInt(1000000000));
        customer.setBirthDate(LocalDate.parse("1969-04-30"));
        customer.setGender(EGender.U);

        return customer;
    }

    public Reading getRandomReading(){
        Reading reading = new Reading();
        reading.setCustomer(getCustomer());
        reading.setSubstitute(true);
        reading.setComment("Marco");
        reading.setDateOfReading(LocalDate.now());
        reading.setMeterCount(100);
        reading.setKindOfMeter(EKindOfMeter.HEIZUNG);
        reading.setMeterId("Polo");

        return reading;
    }

    public Customer getCustomer(){
        return customer;
    }

    public Reading getReading(){
        return reading;
    }

}
