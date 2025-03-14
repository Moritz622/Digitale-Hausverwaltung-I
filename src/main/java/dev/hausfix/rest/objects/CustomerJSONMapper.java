package dev.hausfix.rest.objects;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.User;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.schema.SchemaLoader;
import dev.hausfix.services.ReadingService;
import dev.hausfix.services.UserService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
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

        if(customer.getUser() != null){
            customerJson.put("user", ((User)customer.getUser()).getId().toString());
        }else {
            customerJson.put("user", "");
        }

        mainJson.put("customer", customerJson);

        return mainJson;
    }

    public Customer mapCustomer(JSONObject json){
        Customer customer = new Customer();

        customer.setFirstName(json.get("firstName").toString());
        customer.setLastName(json.get("lastName").toString());
        customer.setGender(EGender.valueOf(json.get("gender").toString()));
        customer.setBirthDate(LocalDate.parse(json.get("birthDate").toString()));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        UserService userService = new UserService(dbConnection);
        ReadingService readingService = new ReadingService(dbConnection);

        try{
            if(json.has("user")){
                if(!json.get("user").toString().matches("")){
                    customer.setUser(userService.getUser(UUID.fromString(json.get("user").toString())));
                }else{
                    customer.setUser(null);
                }
            }else{
                customer.setUser(null);
            }
        }catch(NoEntityFoundException e){
            customer.setUser(null);
        }

        if(json.has("id")){
            customer.setId(UUID.fromString(json.get("id").toString()));
        }

        return customer;
    }

}
