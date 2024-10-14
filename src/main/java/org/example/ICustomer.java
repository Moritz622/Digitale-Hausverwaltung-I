package org.example;

import java.time.LocalDate;

public interface ICustomer {
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setBirthDate(LocalDate birthDate);
    void setGender(EGender gender);

    String getFirstName();
    String getLastName();
    LocalDate getBirthDate();

}