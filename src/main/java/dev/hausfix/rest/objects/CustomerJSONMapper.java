package dev.hausfix.rest.objects;

import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.UUID;

public class CustomerJSONMapper {

    public JSONObject mapCustomer(Customer customer){
        JSONObject mainJson = new JSONObject();

        JSONObject customerJson = new JSONObject();
        customerJson.put("id", customer.getId().toString());
        customerJson.put("firstName", customer.getFirstName());
        customerJson.put("lastName", customer.getLastName());
        customerJson.put("birthDate", customer.getBirthDate().toString());
        customerJson.put("gender", customer.getGender().toString());

        mainJson.put("customer", customerJson);

        return mainJson;
    }

    public Customer mapCustomer(JSONObject json){
        Customer customer = new Customer();

        customer.setFirstName(json.get("firstName").toString());
        customer.setLastName(json.get("lastName").toString());
        customer.setGender(EGender.valueOf(json.get("gender").toString()));
        customer.setBirthDate(LocalDate.parse(json.get("birthDate").toString()));

        if(json.has("id"))
            customer.setId(UUID.fromString(json.get("id").toString()));

        return customer;
    }

}
