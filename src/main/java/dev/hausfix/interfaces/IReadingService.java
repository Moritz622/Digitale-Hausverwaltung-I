package dev.hausfix.interfaces;

import dev.hausfix.entities.Reading;

import java.util.List;
import java.util.UUID;

public interface IReadingService {

    void addReading(Reading reading);

    void removeReading(Reading reading);

    void updateReading(Reading reading);

    List<Reading> getAllReadings();

    Reading getReading(UUID id);

}
