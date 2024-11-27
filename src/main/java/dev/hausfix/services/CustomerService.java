package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.interfaces.ICustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomerService extends Service implements ICustomerService {

    private ReadingService readingService;

    public CustomerService(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public void setReadingService(ReadingService readingService){
        this.readingService = readingService;
    }

    @Override
    public boolean addCustomer(Customer customer) {
        try {
            List<Customer> customers = getAllCustomers().stream().filter(item -> item.getFirstName().equals(customer.getFirstName()) & item.getLastName().equals(customer.getLastName())).collect(Collectors.toList());

            if(!customers.isEmpty()){
                System.out.println("Es ist bereits ein Kunde mit demselben Vor und Nachnamen vorhanden. (ID = " + customers.get(0).getId() + ")");
                return false;
            }

            try {
                getCustomer(customer.getId());
                System.out.println("Es ist schon ein Kunde mit der id " + customer.getId() + " vorhanden!");
            } catch (NoEntityFoundException e) {
                System.out.println("Kein Kunde mit der id " + customer.getId() + " vorhanden!");
            }

            String id = customer.getId().toString();
            String lastname = customer.getLastName();
            String firstname = customer.getFirstName();
            String email = "test";
            String password = "1234";
            String birthdate = customer.getBirthDate().toString();
            EGender gender = customer.getGender();

            if(lastname == null){
                System.out.println("Kunde konnte nicht angelegt werden: Fehlender Eintrag [Nachname] (ID " + id + ")");
                return false;
            }

            if(firstname == null){
                System.out.println("Kunde konnte nicht angelegt werden: Fehlender Eintrag [Vorname] (ID " + id + ")");
                return false;
            }

            if(birthdate == null){
                System.out.println("Kunde konnte nicht angelegt werden: Fehlender Eintrag [geburtsdatum] (ID " + id + ")");
                return false;
            }

            if(gender == null){
                System.out.println("Kunde konnte nicht angelegt werden: Fehlender Eintrag [Geschlecht] (ID " + id + ")");
                return false;
            }

            databaseConnection.getConnection().prepareStatement("INSERT INTO customers (id,lastname,firstname,email,password,birthdate,gender) VALUES ('" + id + "','" + lastname + "','" + firstname + "','" + email + "'," + password + ",DATE('" + birthdate + "'),'" + gender + "');").executeQuery();
        } catch (SQLException e) {
            System.out.println("Kunde konnte nicht hinzugefügt werden.");
        }

        return true;
    }

    @Override
    public void removeCustomer(Customer customer) {
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            List<Reading> readingsOfCustomer = readingService.getAllReadings().stream().filter(item -> ((Customer)item.getCustomer()).getId().equals(customer.getId())).collect(Collectors.toUnmodifiableList());

            for(Reading reading : readingsOfCustomer){
                reading.setCustomer(null);
                readingService.updateReading(reading);
            }

            stmt.executeQuery("DELETE FROM customers WHERE id = '" + customer.getId() + "'");
        } catch (SQLException e) {
            System.out.println("Kein Kunde mit der ID " + customer.getId() + " gefunden");
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("UPDATE customers " +
                    "SET lastname = '" + customer.getLastName() + "'," +
                    "firstname = '" + customer.getFirstName() + "'," +
                    "gender = '" + customer.getGender().toString() + "'," +
                    "birthdate = '" + customer.getBirthDate().toString() + "'" +
                    "WHERE id = '" + customer.getId() + "'");
        } catch (SQLException e) {
            System.out.println("Kein Kunde mit der ID " + customer.getId() + " gefunden");
        }
    }

    @Override
    public ArrayList<Customer> getAllCustomers() {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM customers").executeQuery();

            ArrayList<Customer> customers = new ArrayList<Customer>();

            while(resultsSet.next()){
                Customer customer = new Customer();

                customer.setId(UUID.fromString(resultsSet.getString("id")));
                customer.setLastName(resultsSet.getString("lastname"));
                customer.setFirstName(resultsSet.getString("firstname"));
                customer.setBirthDate(resultsSet.getDate("birthdate").toLocalDate());
                customer.setGender(EGender.valueOf(resultsSet.getString("gender")));

                customers.add(customer);
            }

            return customers;
        } catch (SQLException e) {
            System.out.println("Keine Kunden vorhanden");

            return null;
        }
    }

    @Override
    public Customer getCustomer(UUID id) throws NoEntityFoundException {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM customers WHERE id = '" + id + "'").executeQuery();

            resultsSet.next();

            Customer customer = new Customer();

            customer.setId(UUID.fromString(resultsSet.getString("id")));
            customer.setLastName(resultsSet.getString("lastname"));
            customer.setFirstName(resultsSet.getString("firstname"));
            customer.setBirthDate(resultsSet.getDate("birthdate").toLocalDate());
            customer.setGender(EGender.valueOf(resultsSet.getString("gender")));

            return customer;
        } catch (SQLException e) {
            System.out.println("Kein Kunde mit der ID " + id + " gefunden");

            throw new NoEntityFoundException("No Entity found");
        }
    }
}
