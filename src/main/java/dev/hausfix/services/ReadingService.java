package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.interfaces.ICustomer;
import dev.hausfix.interfaces.IReadingService;
import dev.hausfix.sql.DatabaseConnection;

import javax.swing.plaf.nimbus.State;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingService extends Service implements IReadingService {

    private CustomerService customerService;

    public ReadingService(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public void setCustomerService(CustomerService customerService){
        this.customerService = customerService;
    }

    @Override
    public void addReading(Reading reading) {
        try {
            if(getReading(reading.getId()) != null){
                System.out.println("Es ist schon ein Reading mit der id " + reading.getId() + " vorhanden!");
                return;
            }

            String id = reading.getId().toString();

            if(reading.getDateOfReading() == null){
                System.out.println("Reading konnte nicht angelegt werden: Fehlender Eintrag [Datum] (ID " + id + ")");
                return;
            }

            if(reading.getKindOfMeter() == null){
                System.out.println("Reading konnte nicht angelegt werden: Fehlender Eintrag [Messungs Typ] (ID " + id + ")");
                return;
            }

            if(reading.getMeterId() == null){
                System.out.println("Reading konnte nicht angelegt werden: Fehlender Eintrag [Messgeräts ID] (ID " + id + ")");
                return;
            }

            String comment = reading.getComment();
            String customer;
            String dateOfReading = reading.getDateOfReading().toString();
            String kindOfMeter = reading.getKindOfMeter().toString();
            double meterCount = reading.getMeterCount();
            String meterId = reading.getMeterId();
            int substitute = 0;

            if(reading.getCustomer() == null){
                System.out.println("Es wurde kein Kunde zu dem Reading angegeben, der Eintrag konnte nicht angelegt werden (ID " + reading.getId() + ")");
                return;
            }else{
                customer = ((Customer) reading.getCustomer()).getId().toString();

                if(customerService.getCustomer(UUID.fromString(customer)) == null){
                    if(!customerService.addCustomer((Customer) reading.getCustomer())){
                        System.out.println("Kunde zu Reading " + reading.getId() + " konnte nicht angelegt oder gefunden werden");
                        return;
                    }
                }
            }

            if(reading.getSubstitute()) {
                substitute = 1;
            }

            if(comment == null){
                System.out.printf("Warnung: Kein Kommentar für Reading " + reading.getId() + " angegeben");
                comment = "";
            }

            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("INSERT INTO readings (comment,customerId,dateOfReading,kindOfMeter,meterCount,meterId,substitute,id) VALUES (" +
                    "'" + comment + "',"+
                    "'" + customer + "',"+
                    "'" + dateOfReading + "',"+
                    "'" + kindOfMeter + "',"+
                    "'" + meterCount + "',"+
                    "'" + meterId + "',"+
                    "'" + substitute + "',"+
                    "'" + id + "')");
        } catch (SQLException e) {
            System.out.println("Reading konnte nicht hinzugefügt werden");
        }
    }

    @Override
    public void removeReading(Reading reading) {
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("DELETE FROM readings WHERE id = '" + reading.getId() + "'");
        } catch (SQLException e) {
            System.out.println("Kein Reading mit der ID " + reading.getId() + " gefunden");
        }
    }

    @Override
    public void updateReading(Reading reading) {
        try {
            String comment = reading.getComment();
            String customer;
            String dateOfReading = reading.getDateOfReading().toString();
            String kindOfMeter = reading.getKindOfMeter().toString();
            double meterCount = reading.getMeterCount();
            String meterId = reading.getMeterId();
            int substitute = 0;
            String id = reading.getId().toString();

            if(reading.getCustomer() != null){
                customer = ((Customer) reading.getCustomer()).getId().toString();
            }else{
                customer = "null";
            }

            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("UPDATE readings SET " +
                    "comment = '" + comment + "',"+
                    "customerId = '" + customer + "',"+
                    "dateOfReading = '" + dateOfReading + "',"+
                    "kindOfMeter = '" + kindOfMeter + "',"+
                    "meterCount = '" + meterCount + "',"+
                    "meterId = '" + meterId + "',"+
                    "substitute = '" + substitute + "',"+
                    "id = '" + id + "'" +
                    "WHERE id = '" + reading.getId() + "'");
        } catch (SQLException e) {
            System.out.println("Kein Reading mit der ID " + reading.getId() + " gefunden");
        }
    }

    @Override
    public List<Reading> getAllReadings() {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM readings").executeQuery();

            List<Reading> readings = new ArrayList<Reading>();

            while(resultsSet.next()){
                Reading reading = new Reading();

                reading.setId(UUID.fromString(resultsSet.getString("id")));
                reading.setComment(resultsSet.getString("comment"));
                reading.setCustomer(customerService.getCustomer(UUID.fromString(resultsSet.getString("customerId"))));
                reading.setDateOfReading(LocalDate.parse(resultsSet.getString("dateOfReading")));
                reading.setKindOfMeter(EKindOfMeter.valueOf(resultsSet.getString("kindOfMeter")));
                reading.setMeterCount(Double.parseDouble(resultsSet.getString("meterCount")));
                reading.setMeterId(resultsSet.getString("meterId"));

                if(resultsSet.getBoolean("substitute")){
                    reading.setSubstitute(true);
                }else{
                    reading.setSubstitute(false);
                }

                readings.add(reading);
            }

            return readings;
        } catch (SQLException e) {
            System.out.println("Keine Einträge vorhanden");

            return null;
        }
    }

    @Override
    public Reading getReading(UUID id){
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM readings WHERE id = '" + id.toString() + "'").executeQuery();

            resultsSet.next();

            Reading reading = new Reading();

            if(resultsSet.getString("customerId").matches("null")){
                reading.setCustomer(null);
            }else{
                reading.setCustomer(customerService.getCustomer(UUID.fromString(resultsSet.getString("customerId"))));
            }

            reading.setId(UUID.fromString(resultsSet.getString("id")));
            reading.setComment(resultsSet.getString("comment"));

            reading.setDateOfReading(LocalDate.parse(resultsSet.getString("dateOfReading")));
            reading.setKindOfMeter(EKindOfMeter.valueOf(resultsSet.getString("kindOfMeter")));
            reading.setMeterCount(Double.parseDouble(resultsSet.getString("meterCount")));
            reading.setMeterId(resultsSet.getString("meterId"));

            if(resultsSet.getBoolean("substitute")){
                reading.setSubstitute(true);
            }else{
                reading.setSubstitute(false);
            }

            return reading;
        } catch (SQLException e) {
            System.out.println("Kein Reading mit der ID " + id + " gefunden");

            return null;
        }
    }
}
