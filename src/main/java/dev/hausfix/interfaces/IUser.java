package dev.hausfix.interfaces;

import dev.hausfix.enumerators.EGender;

import java.time.LocalDate;

public interface IUser {
    void setUserame(String username);
    void setEmail(String email);
    void setPassword(String password);

    String getUsername();

    String getPassword();

    String getEmail();

}