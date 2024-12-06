package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
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
    public void addReading(Reading reading) throws DuplicateEntryException, IncompleteDatasetException, SQLException {
        try {
            try {
                getReading(reading.getId());
                throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Datensatz mit der ID vorhanden");
            } catch (NoEntityFoundException e) {

            }

            String id = reading.getId().toString();

            if(reading.getDateOfReading() == null){
                throw new IncompleteDatasetException("Fehlender Eintrag: Datum");
            }

            if(reading.getKindOfMeter() == null){
                throw new IncompleteDatasetException("Fehlender Eintrag: Messtyp");
            }

            if(reading.getMeterId() == null){
                throw new IncompleteDatasetException("Fehlender Eintrag: Messgerät ID");
            }

            String comment = reading.getComment();
            String customer;
            String dateOfReading = reading.getDateOfReading().toString();
            String kindOfMeter = reading.getKindOfMeter().toString();
            double meterCount = reading.getMeterCount();
            String meterId = reading.getMeterId();
            int substitute = 0;

            if(reading.getCustomer() == null){
                throw new IncompleteDatasetException("Fehlender Eintrag: Kunde");
            }else{
                customer = ((Customer) reading.getCustomer()).getId().toString();

                try {
                    customerService.getCustomer(UUID.fromString(customer));
                } catch (NoEntityFoundException e) {
                    try {
                        if(!customerService.addCustomer((Customer) reading.getCustomer())){
                            throw new IncompleteDatasetException("Kunde konnte nicht angelegt werden");
                        }
                    } catch (IncompleteDatasetException ex) {
                        throw ex;
                    }
                }
            }

            if(reading.getSubstitute()) {
                substitute = 1;
            }

            if(comment == null){
                //System.out.printf("Warnung: Kein Kommentar für Reading " + reading.getId() + " angegeben");
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
            throw e;
        }
    }

    @Override
    public void removeReading(Reading reading) throws NoEntityFoundException {
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("DELETE FROM readings WHERE id = '" + reading.getId() + "'");
        } catch (SQLException e) {
            throw new NoEntityFoundException("Kein Eintrag gefunden: es konnte keine Messung mit der ID gefunden werden");
        }
    }

    @Override
    public void updateReading(Reading reading) throws NoEntityFoundException {
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
            throw new NoEntityFoundException("Kein Eintrag gefunden: es konnte keine Messung mit der ID gefunden werden");
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
                try {
                    reading.setCustomer(customerService.getCustomer(UUID.fromString(resultsSet.getString("customerId"))));
                } catch (NoEntityFoundException e) {
                    reading.setCustomer(null);
                }
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
            return null;
        }
    }

    @Override
    public Reading getReading(UUID id) throws NoEntityFoundException {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM readings WHERE id = '" + id.toString() + "'").executeQuery();

            resultsSet.next();

            Reading reading = new Reading();

            if(resultsSet.getString("customerId").matches("null")){
                reading.setCustomer(null);
            }else{
                try {
                    reading.setCustomer(customerService.getCustomer(UUID.fromString(resultsSet.getString("customerId"))));
                } catch (NoEntityFoundException e) {
                    reading.setCustomer(null);
                }
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
            throw new NoEntityFoundException("Kein Eintrag gefunden: es konnte keine Messung mit der ID gefunden werden");
        }
    }
}
