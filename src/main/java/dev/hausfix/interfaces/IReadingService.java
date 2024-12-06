package dev.hausfix.interfaces;

import dev.hausfix.entities.Reading;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface IReadingService {

    void addReading(Reading reading) throws DuplicateEntryException, IncompleteDatasetException, SQLException;

    void removeReading(Reading reading) throws NoEntityFoundException;

    void updateReading(Reading reading) throws NoEntityFoundException;

    List<Reading> getAllReadings();

    Reading getReading(UUID id) throws NoEntityFoundException;

}
