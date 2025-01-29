package dev.hausfix.rest.objects;

import dev.hausfix.entities.User;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.util.Hash;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.UUID;

public class UserJSONMapper {

    public JSONObject mapUser(User user){
        JSONObject mainJson = new JSONObject();

        JSONObject userJson = new JSONObject();
        userJson.put("id", user.getId().toString());
        userJson.put("username", user.getUsername());
        userJson.put("email", user.getEmail());
        userJson.put("password", Hash.hashPassword(user.getPassword()));

        mainJson.put("user", userJson);

        return mainJson;
    }

    public User mapUser(JSONObject json){
        User user = new User();

        user.setUserame(json.get("username").toString());
        user.setPassword(json.get("password").toString());
        user.setEmail(json.get("email").toString());

        if(json.has("id"))
            user.setId(UUID.fromString(json.get("id").toString()));

        return user;
    }

}
