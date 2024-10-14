package org.example;

import java.time.LocalDate;

public interface IReading {
    // Setter methods
    void setComment (String comment);
    void setCustomer(ICustomer customer);
    void setDateOfReading(LocalDate dateOfReading);
    void setKindOfMeter(KindOfMeter kindOfMeter);
    void setMeterCount(double meterCount);
    void setMeterId(String meterId);
    void setSubstitute(boolean substitute);

    // Getter methods
    String getComment();
    ICustomer getCustomer();
    LocalDate getDateOfReading();
    KindOfMeter getKindOfMeter();
    double getMeterCount();
    String getMeterId();
    boolean getSubstitute();

    // Custom method to print the date of reading
    String printDateOfReading();
}