package dev.hausfix.interfaces;

import dev.hausfix.enumerators.EGender;

import java.time.LocalDate;

public interface ICustomer {
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setBirthDate(LocalDate birthDate);
    void setGender(EGender gender);
    void setUser(IUser user);

    String getFirstName();
    String getLastName();
    LocalDate getBirthDate();
    EGender getGender();
    IUser getUser();

}