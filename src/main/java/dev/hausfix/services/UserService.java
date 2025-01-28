package dev.hausfix.services;

import dev.hausfix.entities.User;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.interfaces.IUserService;
import dev.hausfix.sql.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserService extends Service implements IUserService {

    private ReadingService readingService;

    public UserService(DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public void setReadingService(ReadingService readingService){
        this.readingService = readingService;
    }

    @Override
    public boolean addUser(User user) throws IncompleteDatasetException, DuplicateEntryException {
        List<User> users = getAllUsers().stream().filter(item -> item.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        if(!users.isEmpty()){
            throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Kunde mit demselben Vor und Nachnamen vorhanden");
        }

        try {
            getUser(user.getId());
            throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Kunde mit der ID vorhanden");
        } catch (NoEntityFoundException e) {}

        String id = user.getId().toString();
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();

        if(username == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Nachname");
        }

        if(email == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Email");
        }

        if(password == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Passwort");
        }

        try {
            databaseConnection.getConnection().prepareStatement("INSERT INTO users (id,username,email,password) VALUES ('" + id + "','" + username + "','" + email + "'," + password + "');").executeQuery();
        } catch (SQLException e) {
            System.out.println("User konnte nicht hinzugefügt werden.");
            return false;
        }

        return true;
    }

    @Override
    public void removeUser(User user) throws NoEntityFoundException {
        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("DELETE FROM users WHERE id = '" + user.getId() + "'");
        } catch (SQLException e) {
            throw new NoEntityFoundException("Es konnte kein User mit der ID gefunden werden");
        }
    }

    @Override
    public void updateUser(User user) throws NoEntityFoundException, IncompleteDatasetException, DuplicateEntryException {
        List<User> users = getAllUsers().stream().filter(item -> item.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        if(!users.isEmpty()){
            throw new DuplicateEntryException("Doppelter Eintrag: Es ist bereits ein Kunde mit demselben Vor und Nachnamen vorhanden");
        }

        String id = user.getId().toString();
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();

        if(username == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Nachname");
        }

        if(email == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Email");
        }

        if(password == null){
            throw new IncompleteDatasetException("Fehlender Eintrag: Passwort");
        }

        try {
            Statement stmt = databaseConnection.getConnection().createStatement();

            stmt.executeQuery("UPDATE users " +
                    "SET username = '" + username + "'," +
                    "email = '" + email + "'," +
                    "password = '" + password + "'" +
                    "WHERE id = '" + id + "'");
        } catch (SQLException e) {
            throw new NoEntityFoundException("Es konnte kein Kunde mit der ID gefunden werden");
        }
    }

    @Override
    public ArrayList<User> getAllUsers() {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM users").executeQuery();

            ArrayList<User> users = new ArrayList<User>();

            while(resultsSet.next()){
                User user = new User();

                user.setId(UUID.fromString(resultsSet.getString("id")));
                user.setUserame(resultsSet.getString("username"));
                user.setEmail(resultsSet.getString("email"));
                user.setPassword(resultsSet.getString("password"));

                users.add(user);
            }

            return users;
        } catch (SQLException e) {
            System.out.println("Keine Kunden vorhanden");

            return null;
        }
    }

    @Override
    public User getUser(UUID id) throws NoEntityFoundException {
        try {
            ResultSet resultsSet = databaseConnection.getConnection().prepareStatement("SELECT * FROM users WHERE id = '" + id + "'").executeQuery();

            resultsSet.next();

            User user = new User();

            user.setId(UUID.fromString(resultsSet.getString("id")));
            user.setUserame(resultsSet.getString("username"));
            user.setEmail(resultsSet.getString("email"));
            user.setPassword(resultsSet.getString("password"));

            return user;
        } catch (SQLException e) {
            throw new NoEntityFoundException("Es konnte kein Kunde mit der ID gefunden werden");
        }
    }
}
