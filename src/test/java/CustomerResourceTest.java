import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.rest.ressource.CustomerRessource;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.Helper;
import dev.hausfix.util.PropertyLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;

public class CustomerResourceTest {

    @Test
    public void getCustomers() {
        CustomerRessource customerRessource = new CustomerRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);
        dbConnection.truncateAllTables();

        Helper helper = new Helper();
        Customer customer = helper.getCustomer();
        try {
            customerService.addCustomer(customer);
        } catch (IncompleteDatasetException e) {
            throw new RuntimeException(e);
        } catch (DuplicateEntryException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = new JSONObject(customerRessource.getCustomer().getEntity().toString());
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();
        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("customers"));

        Customer marco = customerJSONMapper.mapCustomer(jsonArray.getJSONObject(0));

        assertEquals("Marco",marco.getFirstName());
        assertEquals("Polo",marco.getLastName());
    }

    @Test
    public void addCustomer(){
        CustomerRessource customerRessource = new CustomerRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);
        dbConnection.truncateAllTables();

        Helper helper = new Helper();
        Customer customer = helper.getCustomer();

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        customerRessource.addCustomer(customerJSONMapper.mapCustomer(customer).get("customer").toString());

        try {
            Customer marco = customerService.getCustomer(customer.getId());

            assertEquals("Marco",marco.getFirstName());
        } catch (NoEntityFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void putCustomer(){
        CustomerRessource customerRessource = new CustomerRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        CustomerService customerService = new CustomerService(dbConnection);
        dbConnection.truncateAllTables();

        Helper helper = new Helper();
        Customer customer = helper.getCustomer();

        try {
            customerService.addCustomer(customer);
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            throw new RuntimeException(e);
        }

        customer.setFirstName("Nina");
        customer.setLastName("Higga");
        customer.setGender(EGender.W);

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        customerRessource.putCustomer(customerJSONMapper.mapCustomer(customer).get("customer").toString());

        try {
            customer = customerService.getCustomer(customer.getId());
        } catch (NoEntityFoundException e) {
            throw new RuntimeException(e);
        }

        assertEquals("Nina", customer.getFirstName());
    }

}
