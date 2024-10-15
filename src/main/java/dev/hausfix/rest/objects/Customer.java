package dev.hausfix.rest.objects;

import dev.hausfix.enumerators.EGender;
import dev.hausfix.interfaces.ICustomer;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;

@XmlRootElement
public class Customer implements ICustomer {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private EGender gender;
    private String email;
    private String password;

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
