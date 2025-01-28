import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.ReadingJSONMapper;
import dev.hausfix.rest.ressource.ReadingRessource;
import dev.hausfix.rest.ressource.ReadingRessource;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.Helper;
import dev.hausfix.util.PropertyLoader;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReadingResourceTest {

    private static Helper helper;

    @BeforeAll
    public static void init(){
        helper = new Helper();
    }

    @Test
    @Order(1)
    public void addReading(){
        ReadingRessource readingRessource = new ReadingRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        dbConnection.truncateAllTables();

        Reading reading = helper.getReading();
        Customer customer = helper.getCustomer();

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        try {
            customerService.addCustomer(customer);
        } catch (IncompleteDatasetException e) {
            throw new RuntimeException(e);
        } catch (DuplicateEntryException e) {
            throw new RuntimeException(e);
        }

        reading.setCustomer(customer);

        readingRessource.addReading(readingJSONMapper.mapReading(reading).get("reading").toString());

        try {
            Reading marco = readingService.getReading(reading.getId());

            assertEquals(helper.getReading().getComment(), marco.getComment());
        } catch (NoEntityFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void getReadings() throws IncompleteDatasetException, SQLException, DuplicateEntryException {
        ReadingRessource readingRessource = new ReadingRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        JSONObject jsonObject = new JSONObject(readingRessource.getReading(helper.readingUUID.toString()).getEntity().toString());
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        Reading r = readingJSONMapper.mapReading(jsonObject);

        assertEquals(helper.getReading().getComment(), r.getComment());
        assertEquals(helper.getReading().getMeterId(), r.getMeterId());
    }

    @Test
    @Order(3)
    public void getReading(){
        ReadingRessource readingRessource = new ReadingRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        JSONObject jsonObject = new JSONObject(readingRessource.getReadingsByCriteria(helper.customerUUID.toString(), helper.reading.getDateOfReading().toString(), helper.reading.getDateOfReading().toString(), helper.reading.getKindOfMeter().toString()).getEntity().toString());

        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("readings"));

        Reading reading = readingJSONMapper.mapReading(jsonArray.getJSONObject(0));

        assertEquals(helper.reading.getComment(), reading.getComment());

        jsonObject = new JSONObject(readingRessource.getReadingsByCriteria(helper.customerUUID.toString(), null, helper.reading.getDateOfReading().toString(), helper.reading.getKindOfMeter().toString()).getEntity().toString());

        jsonObject = new JSONObject(readingRessource.getReadingsByCriteria(helper.customerUUID.toString(), helper.reading.getDateOfReading().toString(), null, helper.reading.getKindOfMeter().toString()).getEntity().toString());

        readingRessource.getReadingsByCriteria(null, helper.reading.getDateOfReading().toString(), null, helper.reading.getKindOfMeter().toString()).getEntity().toString();

        jsonObject = new JSONObject(readingRessource.getReadingsByCriteria(helper.customerUUID.toString(), helper.reading.getDateOfReading().toString(), helper.reading.getDateOfReading().toString(), null));
    }

    @Test
    @Order(4)
    public void putReading(){
        ReadingRessource readingRessource = new ReadingRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        ReadingService readingService = new ReadingService(dbConnection);
        CustomerService customerService = new CustomerService(dbConnection);

        readingService.setCustomerService(customerService);
        customerService.setReadingService(readingService);

        JSONObject jsonObject = null;
        jsonObject = new JSONObject(readingRessource.getReading(helper.readingUUID.toString()).getEntity().toString());
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();
        Reading r = readingJSONMapper.mapReading(jsonObject);

        r.setComment("Nina");
        r.setMeterCount(420);

        readingRessource.putReading(readingJSONMapper.mapReading(r).get("reading").toString());

        try {
            r = readingService.getReading(r.getId());
        } catch (NoEntityFoundException e) {
            throw new RuntimeException(e);
        }

        assertEquals("Nina", r.getComment());

        r.setId(UUID.fromString("76fa051f-83ee-48c1-9bca-9e4468b29d08"));

        readingRessource.putReading(readingJSONMapper.mapReading(r).get("reading").toString());
    }

}
