package dev.hausfix.services;

import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.entities.User;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.interfaces.ICustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
    public boolean addCustomer(Customer customer) throws IncompleteDatasetException, DuplicateEntryException {
        if(isNameUsed(customer)){
            throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Kunde mit demselben Vor und Nachnamen vorhanden");
        }

        try {
            getCustomer(customer.getId());
            throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Kunde mit der ID vorhanden");
        } catch (NoEntityFoundException e) {}

        String id = customer.getId().toString();
        String lastname = customer.getLastName();
        String firstname = customer.getFirstName();
        String email = "test";
        String password = "1234";
        String birthdate = customer.getBirthDate().toString();
        EGender gender = customer.getGender();
        String userID;

        User user = (User)customer.getUser();

        if(lastname == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Nachname");
        }

        if(firstname == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Vorname");
        }

        if(user == null){
            userID = "";
        }else{
            userID = user.getId().toString();
        }

        if(gender == null){
            gender = EGender.U;
        }

        try {
            databaseConnection.getConnection().prepareStatement("INSERT INTO customers (id,lastname,firstname,email,password,userid,birthdate,gender) VALUES ('" + id + "','" + lastname + "','" + firstname + "','" + email + "','" + password + "','" + userID + "',DATE('" + birthdate + "'),'" + gender  + "');").executeQuery();
        } catch (SQLException e) {
            System.out.println("Kunde konnte nicht hinzugefügt werden." + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void removeCustomer(Customer customer) throws NoEntityFoundException {
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            System.out.println(readingService);

            List<Reading> readingsOfCustomer = readingService.getReadingsByCriteria(customer.getId(), LocalDate.MIN, LocalDate.MAX, null);

            for(Reading reading : readingsOfCustomer){
                reading.setCustomer(null);
                readingService.updateReading(reading);
            }

            stmt.executeQuery("DELETE FROM customers WHERE id = '" + customer.getId() + "'");
        } catch (SQLException e) {
            throw new NoEntityFoundException("Es konnte kein Kunde mit der ID gefunden werden");
        }
    }

    private boolean isNameUsed(Customer customer){
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE firstname = '" + customer.getFirstName() + "' AND lastname = '" + customer.getLastName() + "'");

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws NoEntityFoundException, IncompleteDatasetException, DuplicateEntryException {
        if(isNameUsed(customer)){
            throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Kunde mit demselben Vor und Nachnamen vorhanden");
        }

        String id = customer.getId().toString();
        String lastname = customer.getLastName();
        String firstname = customer.getFirstName();
        String birthdate = null;
        String userID = "";

        User user = (User)customer.getUser();

        if(user == null){
            userID = "";
        }else{
            userID = user.getId().toString();
        }

        if(customer.getBirthDate() != null){
            birthdate = customer.getBirthDate().toString();
        }

        EGender gender = customer.getGender();

        if(lastname == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Nachname");
        }

        if(firstname == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Vorname");
        }

        if(customer.getBirthDate() == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Geburtsdatum");
        }

        if(gender == null){
            gender = EGender.U;
        }

        if(userID == null){
            userID = "";
        }

        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("UPDATE customers " +
                    "SET lastname = '" + lastname + "'," +
                    "firstname = '" + firstname + "'," +
                    "userid = '" + userID + "'," +
                    "gender = '" + gender + "'," +
                    "birthdate = '" + birthdate + "' " +
                    "WHERE id = '" + id + "'");
        } catch (SQLException e) {
            throw new NoEntityFoundException("Es konnte kein Kunde mit der ID gefunden werden " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Customer> getAllCustomers() {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM customers").executeQuery();

            ArrayList<Customer> customers = new ArrayList<Customer>();

            UserService userService = new UserService(databaseConnection);

            while(resultsSet.next()){
                Customer customer = new Customer();

                customer.setId(UUID.fromString(resultsSet.getString("id")));
                customer.setLastName(resultsSet.getString("lastname"));
                customer.setFirstName(resultsSet.getString("firstname"));
                customer.setBirthDate(resultsSet.getDate("birthdate").toLocalDate());
                customer.setGender(EGender.valueOf(resultsSet.getString("gender")));

                try{
                    customer.setUser(userService.getUser(UUID.fromString(resultsSet.getString("userid"))));
                }catch(Exception e){
                    customer.setUser(null);
                }

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

            UserService userService = new UserService(databaseConnection);

            try{
                customer.setUser(userService.getUser(UUID.fromString(resultsSet.getString("userid"))));
            }catch(Exception e){
                customer.setUser(null);
            }

            return customer;
        } catch (SQLException e) {
            throw new NoEntityFoundException("Es konnte kein Kunde mit der ID gefunden werden");
        }
    }
}
