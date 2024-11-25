package dev.hausfix.entities;

import com.sun.jna.platform.win32.WinUser;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.interfaces.ICustomer;
import dev.hausfix.interfaces.IID;

import java.time.LocalDate;
import java.util.UUID;

public class Customer extends Entity implements ICustomer {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private EGender gender;
    private UUID id;

    public Customer(){
        id = UUID.randomUUID();
    }

    @Override
    public UUID getId() {
        if(id == null)
            id = UUID.randomUUID();

        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public void setGender(EGender gender) {
        this.gender = gender;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public EGender getGender() {
        return gender;
    }
}
