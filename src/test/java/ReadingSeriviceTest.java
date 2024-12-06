import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReadingServiceIntegrationTest {

    private DatabaseConnection dbConnection;
    private ReadingService readingService;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        dbConnection = new DatabaseConnection();
        Properties properties = new PropertyLoader().getProperties("src/main/resources/hausfix.properties");
        dbConnection.openConnection(properties);
        dbConnection.truncateAllTables(); // Leert die Datenbank vor jedem Test

        customerService = new CustomerService(dbConnection);
        readingService = new ReadingService(dbConnection);
        readingService.setCustomerService(customerService); // Dependency Injection
        customerService.setReadingService(readingService);
    }

    @Test
    void testAddReading() throws DuplicateEntryException, IncompleteDatasetException, SQLException, NoEntityFoundException {
        // Arrange
        Customer customer = createTestCustomer();
        customerService.addCustomer(customer);

        Reading reading = createTestReading(customer);

        // Act
        readingService.addReading(reading);

        // Assert
        Reading retrievedReading = readingService.getReading(reading.getId());
        assertEquals(reading.getMeterId(), retrievedReading.getMeterId());
        assertEquals(reading.getMeterCount(), retrievedReading.getMeterCount());
    }

    @Test
    void testAddReadingWithMissingCustomer() throws DuplicateEntryException, IncompleteDatasetException, SQLException {
        // Arrange
        Reading reading = createTestReadingWithoutCustomer();  // Create a reading without a customer

        // Act & Assert
        assertThrows(IncompleteDatasetException.class, () -> readingService.addReading(reading));
    }

    @Test
    void testRemoveReading() throws DuplicateEntryException, IncompleteDatasetException, SQLException, NoEntityFoundException {
        // Arrange
        Customer customer = createTestCustomer();
        customerService.addCustomer(customer);
        Reading reading = createTestReading(customer);
        readingService.addReading(reading);

        // Act
        readingService.removeReading(reading);

        // Assert
        assertThrows(NoEntityFoundException.class, () -> readingService.getReading(reading.getId()));
    }

    @Test
    void testUpdateReading() throws DuplicateEntryException, IncompleteDatasetException, SQLException, NoEntityFoundException {
        // Arrange
        Customer customer = createTestCustomer();
        customerService.addCustomer(customer);
        Reading reading = createTestReading(customer);
        readingService.addReading(reading);

        // Update
        reading.setMeterCount(500.0);
        readingService.updateReading(reading);

        // Act
        Reading updatedReading = readingService.getReading(reading.getId());

        // Assert
        assertEquals(500.0, updatedReading.getMeterCount());
    }

    @Test
    void testGetAllReadings() throws DuplicateEntryException, IncompleteDatasetException, SQLException {
        // Arrange
        Customer customer = createTestCustomer();
        customerService.addCustomer(customer);

        Reading reading1 = createTestReading(customer);
        Reading reading2 = createTestReading(customer);
        reading2.setId(UUID.randomUUID());
        readingService.addReading(reading1);
        readingService.addReading(reading2);

        // Act
        List<Reading> readings = readingService.getAllReadings();

        // Assert
        assertEquals(2, readings.size());
    }

    @Test
    void testGetReadingNotFound() {
        // Arrange
        UUID fakeId = UUID.randomUUID();

        // Act & Assert
        assertThrows(NoEntityFoundException.class, () -> readingService.getReading(fakeId));
    }

    // Hilfsmethoden

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setGender(EGender.M);
        return customer;
    }

    private Reading createTestReading(Customer customer) {
        Reading reading = new Reading();
        reading.setId(UUID.randomUUID());
        reading.setCustomer(customer);
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(EKindOfMeter.Strom);
        reading.setMeterCount(250.0);
        reading.setMeterId("12345ABC");
        reading.setSubstitute(false);
        return reading;
    }

    private Reading createTestReadingWithoutCustomer() {
        Reading reading = new Reading();
        reading.setId(UUID.randomUUID());
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(EKindOfMeter.Strom);  // Set appropriate meter type
        reading.setMeterId("meter123");
        reading.setMeterCount(100.5);
        reading.setComment("Test reading without customer");
        reading.setSubstitute(false);

        // Don't set the customer
        return reading;
    }
}
