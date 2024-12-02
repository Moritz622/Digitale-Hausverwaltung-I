package dev.hausfix.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jna.platform.win32.WinUser;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.interfaces.ICustomer;
import dev.hausfix.interfaces.IID;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.util.UUID;

@XmlRootElement
public class Customer extends Entity implements ICustomer {

    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @JsonProperty("gender")
    private EGender gender;
    @JsonProperty("id")
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
    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @Override
    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @Override
    @JsonProperty("birthDate")
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    @JsonProperty("gender")
    public EGender getGender() {
        return gender;
    }
}
