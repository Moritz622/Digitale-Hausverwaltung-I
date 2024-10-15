package dev.hausfix.interfaces;

import java.time.LocalDate;

import dev.hausfix.enumerators.EKindOfMeter;

public interface IReading {
    // Setter methods
    void setComment (String comment);
    void setCustomer(ICustomer customer);
    void setDateOfReading(LocalDate dateOfReading);
    void setKindOfMeter(EKindOfMeter kindOfMeter);
    void setMeterCount(double meterCount);
    void setMeterId(String meterId);
    void setSubstitute(boolean substitute);

    // Getter methods
    String getComment();
    ICustomer getCustomer();
    LocalDate getDateOfReading();
    EKindOfMeter getKindOfMeter();
    double getMeterCount();
    String getMeterId();
    boolean getSubstitute();

    // Custom method to print the date of reading
    String printDateOfReading();
}