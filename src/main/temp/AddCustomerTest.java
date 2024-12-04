import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.enumerators.EKindOfMeter;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class AddCustomerTest {

    /*
    private CustomerService customerService;
    private ReadingService readingService;

    @Test
    public void testMyMethod() {
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
            nina.setGender(EGender.W);
            nina.setBirthDate(LocalDate.now());

            customerService.addCustomer(nina);

            try {
                assertEquals("Nina", customerService.getCustomer(nina.getId()).getFirstName());
                assertEquals("Markart", customerService.getCustomer(nina.getId()).getLastName());
                assertEquals(EGender.W, customerService.getCustomer(nina.getId()).getGender());
                assertEquals(LocalDate.now(), customerService.getCustomer(nina.getId()).getBirthDate());
            } catch (NoEntityFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = NoEntityFoundException.class)
    public void addCustomerNoEntityFound() throws NoEntityFoundException {
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

            customerService.getCustomer(nina.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = NoEntityFoundException.class)
    public void deleteCustomer() throws NoEntityFoundException {
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
            nina.setGender(EGender.W);
            nina.setBirthDate(LocalDate.now());

            customerService.addCustomer(nina);

            customerService.removeCustomer(nina);

            customerService.getCustomer(nina.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateCustomer() {
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

            customerService.addCustomer(nina);

            nina.setLastName("Marnig");
            nina.setFirstName("Niga");
            nina.setGender(EGender.W);

            customerService.updateCustomer(nina);

            try {
                assertEquals("Niga", customerService.getCustomer(nina.getId()).getFirstName());
                assertEquals("Marnig", customerService.getCustomer(nina.getId()).getLastName());
                assertEquals(EGender.W, customerService.getCustomer(nina.getId()).getGender());
            } catch (NoEntityFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/
}
