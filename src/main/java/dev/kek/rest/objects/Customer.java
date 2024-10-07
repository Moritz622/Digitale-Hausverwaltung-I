package dev.kek.rest.objects;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;

@XmlRootElement
public class Customer {
    private String name;
    private String surname;
    private Date dateOfBirth;
    private int genderID;
    private String email;
    private String password;

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGenderID(int genderID) {
        this.genderID = genderID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public int getGenderID() {
        return genderID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
