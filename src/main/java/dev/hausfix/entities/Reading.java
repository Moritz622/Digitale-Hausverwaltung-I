package dev.hausfix.entities;

import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.interfaces.ICustomer;
import dev.hausfix.interfaces.IID;
import dev.hausfix.interfaces.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading extends Entity implements IReading {

    private String comment;
    private ICustomer customer;
    private LocalDate dateOfReading;
    private EKindOfMeter kindOfMeter;
    private double meterCount;
    private String meterId;
    private boolean substitute;
    private UUID id;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCustomer(ICustomer customer) {
        this.customer = customer;
    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    @Override
    public void setKindOfMeter(EKindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    @Override
    public void setMeterCount(double meterCount) {
        this.meterCount = meterCount;
    }

    @Override
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    @Override
    public void setSubstitute(boolean substitute) {
        this.substitute = substitute;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public ICustomer getCustomer() {
        return customer;
    }

    @Override
    public LocalDate getDateOfReading() {
        return dateOfReading;
    }

    @Override
    public EKindOfMeter getKindOfMeter() {
        return kindOfMeter;
    }

    @Override
    public double getMeterCount() {
        return meterCount;
    }

    @Override
    public String getMeterId() {
        return meterId;
    }

    @Override
    public boolean getSubstitute() {
        return substitute;
    }

    @Override
    public String printDateOfReading() {
        return dateOfReading.toString();
    }
}
