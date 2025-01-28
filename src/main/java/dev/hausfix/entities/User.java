package dev.hausfix.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.interfaces.ICustomer;
import dev.hausfix.interfaces.IUser;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.util.UUID;

@XmlRootElement
public class User extends Entity implements IUser {

    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("id")
    private UUID id;

    public User(){
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
    public void setUserame(String username) {
        this.username = username;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
