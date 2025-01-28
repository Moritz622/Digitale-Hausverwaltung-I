package dev.hausfix.interfaces;

import dev.hausfix.entities.User;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    boolean addUser(User user) throws IncompleteDatasetException, DuplicateEntryException;

    void removeUser(User user) throws NoEntityFoundException;

    void updateUser(User user) throws NoEntityFoundException, IncompleteDatasetException, DuplicateEntryException;

    List<User> getAllUsers();

    User getUser(UUID id) throws NoEntityFoundException;

}
