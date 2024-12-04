import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ReadingsTest {
/*
    private CustomerService customerService;
    private ReadingService readingService;

    @Before
    public void setup(){

    }

    @Test
    public void AddReading() {
        Properties properties = new Properties();

        try(InputStream input = new FileInputStream("src/main/resources/hausfix.properties")) {
            properties.load(input);

            DatabaseConnection connection = (DatabaseConnection) new DatabaseConnection().openConnection(properties);
            connection.removeAllTables();
            connection.createAllTables();

            CustomerService customerService = new CustomerService(connection);
            ReadingService readingService = new ReadingService(connection);

            customerService.setReadingService(readingService);
            readingService.setCustomerService(customerService);

            Customer nina = new Customer();
            nina.setFirstName("Nina");
            nina.setLastName("Markart");
            nina.setGender(EGender.D);
            nina.setBirthDate(LocalDate.now());

            Reading reading = new Reading();
            reading.setCustomer(nina);

            readingService.addReading(reading);

            assertEquals("Nina", readingService.getReading(reading.getId()).getCustomer().getFirstName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoEntityFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = NoEntityFoundException.class)
    public void addReadingNoEntityFound() throws NoEntityFoundException {
        Properties properties = new Properties();

        try(InputStream input = new FileInputStream("src/main/resources/hausfix.properties")) {
            properties.load(input);

            DatabaseConnection connection = (DatabaseConnection) new DatabaseConnection().openConnection(properties);
            connection.removeAllTables();
            connection.createAllTables();

            CustomerService customerService = new CustomerService(connection);
            ReadingService readingService = new ReadingService(connection);

            customerService.setReadingService(readingService);
            readingService.setCustomerService(customerService);

            Reading reading = new Reading();

            readingService.getReading(reading.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = NoEntityFoundException.class)
    public void deleteReading() throws NoEntityFoundException {
        Properties properties = new Properties();

        try(InputStream input = new FileInputStream("src/main/resources/hausfix.properties")) {
            properties.load(input);

            DatabaseConnection connection = (DatabaseConnection) new DatabaseConnection().openConnection(properties);
            connection.removeAllTables();
            connection.createAllTables();

            CustomerService customerService = new CustomerService(connection);
            ReadingService readingService = new ReadingService(connection);

            customerService.setReadingService(readingService);
            readingService.setCustomerService(customerService);

            Reading reading = new Reading();

            readingService.addReading(reading);

            readingService.removeReading(reading);

            readingService.getReading(reading.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateReading() {
        Properties properties = new Properties();

        try(InputStream input = new FileInputStream("src/main/resources/hausfix.properties")) {
            properties.load(input);

            DatabaseConnection connection = (DatabaseConnection) new DatabaseConnection().openConnection(properties);
            connection.removeAllTables();
            connection.createAllTables();

            CustomerService customerService = new CustomerService(connection);
            ReadingService readingService = new ReadingService(connection);

            customerService.setReadingService(readingService);
            readingService.setCustomerService(customerService);

            Reading reading = new Reading();
            reading.setComment("kek");

            readingService.addReading(reading);

            reading.setComment("juasd");

            readingService.updateReading(reading);

            try {
                assertEquals("juasd", readingService.getReading(reading.getId()));
            } catch (NoEntityFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/
}
