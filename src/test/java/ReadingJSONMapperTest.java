import dev.hausfix.entities.Customer;
import dev.hausfix.entities.Reading;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.rest.objects.ReadingJSONMapper;
import dev.hausfix.rest.ressource.CustomerRessource;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.Helper;
import dev.hausfix.util.PropertyLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;

public class ReadingJSONMapperTest {

    private static Helper helper;

    @BeforeAll
    public static void init(){
        helper = new Helper();

        CustomerRessource customerRessource = new CustomerRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);
        dbConnection.truncateAllTables();

        Customer customer = helper.getCustomer();

        try {
            customerService.addCustomer(customer);
        } catch (IncompleteDatasetException e) {
            throw new RuntimeException(e);
        } catch (DuplicateEntryException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void JSONToEntity(){
        ReadingJSONMapper readingJSONMapper = new ReadingJSONMapper();

        JSONObject jsonObject1 = readingJSONMapper.mapReading(helper.getReading()).getJSONObject("reading");
        JSONObject jsonObject2 = readingJSONMapper.mapReading(helper.getReading()).getJSONObject("reading");

        jsonObject2.remove("id");

        Reading reading1 = readingJSONMapper.mapReading(jsonObject1);
        Reading reading2 = readingJSONMapper.mapReading(jsonObject2);

        reading2.setId(null);
        reading2.getId();

        assertEquals(reading1.getId().toString(), helper.readingUUID.toString());
        assertEquals(reading2.getComment(), helper.reading.getComment());
    }

}
