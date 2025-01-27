import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;

import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceIntegrationTest {

    private ReadingService readingService;
    private DatabaseConnection dbConnection;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        dbConnection = new DatabaseConnection();
        Properties properties = new PropertyLoader().getProperties("src/main/resources/hausfix.properties");
        dbConnection.openConnection(properties);
        dbConnection.truncateAllTables(); // Leert die Datenbank für jeden Test
        customerService = new CustomerService(dbConnection);
        readingService = new ReadingService(dbConnection);

        customerService.setReadingService(readingService);
        readingService.setCustomerService(customerService);
    }

    @Test
    void testAddCustomer() throws IncompleteDatasetException, DuplicateEntryException {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setGender(EGender.M);

        // Act
        boolean result = customerService.addCustomer(customer);

        // Assert
        assertTrue(result);
        assertDoesNotThrow(() -> customerService.getCustomer(customer.getId()));
    }

    @Test
    void testAddDuplicateCustomerThrowsException() throws IncompleteDatasetException, DuplicateEntryException {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Anna");
        customer.setLastName("Müller");
        customer.setBirthDate(LocalDate.of(1985, 5, 20));
        customer.setGender(EGender.W);

        customerService.addCustomer(customer);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> customerService.addCustomer(customer));
    }

    @Test
    void testGetCustomer() throws IncompleteDatasetException, DuplicateEntryException, NoEntityFoundException {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Lisa");
        customer.setLastName("Schmidt");
        customer.setBirthDate(LocalDate.of(2000, 12, 12));
        customer.setGender(EGender.W);

        customerService.addCustomer(customer);

        // Act
        Customer retrievedCustomer = customerService.getCustomer(customer.getId());

        // Assert
        assertEquals(customer.getFirstName(), retrievedCustomer.getFirstName());
        assertEquals(customer.getLastName(), retrievedCustomer.getLastName());
    }

    @Test
    void testRemoveCustomer() throws IncompleteDatasetException, DuplicateEntryException, NoEntityFoundException {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Tim");
        customer.setLastName("Koch");
        customer.setBirthDate(LocalDate.of(1995, 3, 3));
        customer.setGender(EGender.M);

        customerService.addCustomer(customer);

        // Act
        customerService.removeCustomer(customer);

        // Assert
        assertThrows(NoEntityFoundException.class, () -> customerService.getCustomer(customer.getId()));
    }

    @Test
    void testUpdateCustomer() throws IncompleteDatasetException, DuplicateEntryException, NoEntityFoundException {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Sara");
        customer.setLastName("Meier");
        customer.setBirthDate(LocalDate.of(1998, 7, 15));
        customer.setGender(EGender.W);

        customerService.addCustomer(customer);

        // Update Daten
        customer.setFirstName("Sarah");
        customerService.updateCustomer(customer);

        // Act
        Customer updatedCustomer = customerService.getCustomer(customer.getId());

        // Assert
        assertEquals("Sarah", updatedCustomer.getFirstName());
    }

    @Test
    void testGetAllCustomers() throws IncompleteDatasetException, DuplicateEntryException {
        // Arrange
        Customer customer1 = new Customer();
        customer1.setId(UUID.randomUUID());
        customer1.setFirstName("Paul");
        customer1.setLastName("Huber");
        customer1.setBirthDate(LocalDate.of(1980, 11, 11));
        customer1.setGender(EGender.M);

        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setFirstName("Laura");
        customer2.setLastName("Weber");
        customer2.setBirthDate(LocalDate.of(1993, 8, 21));
        customer2.setGender(EGender.W);

        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);

        // Act
        var customers = customerService.getAllCustomers();

        // Assert
        assertNotNull(customers);
        assertEquals(2, customers.size());
    }
}
