package dev.hausfix.rest.objects;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.schema.SchemaLoader;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;


public class ReadingJSONMapper {

    public JSONObject mapReading(Reading reading){
        JSONObject mainJson = new JSONObject();

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        JSONObject readingJson = new JSONObject();
        readingJson.put("id", reading.getId().toString());

        if(reading.getCustomer() != null){
            readingJson.put("customer", SchemaLoader.load(customerJSONMapper.mapCustomer((Customer) reading.getCustomer()), "schema/CustomerJsonSchema.json").get("customer"));
        }
        readingJson.put("dateOfReading", reading.getDateOfReading().toString());
        readingJson.put("kindOfMeter", reading.getKindOfMeter().toString());
        readingJson.put("meterCount", reading.getMeterCount());
        readingJson.put("meterId", reading.getMeterId());
        readingJson.put("comment", reading.getComment());
        readingJson.put("substitute", reading.getSubstitute());

        mainJson.put("reading", readingJson);

        return mainJson;
    }

    public Reading mapReading(JSONObject json) {
        Reading reading = new Reading();

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        try {
            reading.setCustomer(customerService.getCustomer(UUID.fromString(json.getJSONObject("customer").get("id").toString())));
        } catch (NoEntityFoundException e) {

        }

        reading.setDateOfReading(LocalDate.parse(json.get("dateOfReading").toString()));
        reading.setKindOfMeter(EKindOfMeter.valueOf(json.get("kindOfMeter").toString()));
        reading.setMeterCount(Double.parseDouble(json.get("meterCount").toString()));
        reading.setMeterId(json.get("meterId").toString());
        reading.setComment(json.get("comment").toString());
        reading.setSubstitute(Boolean.parseBoolean(json.get("substitute").toString()));

        if(json.has("id"))
            reading.setId(UUID.fromString(json.get("id").toString()));

        return reading;
    }

}