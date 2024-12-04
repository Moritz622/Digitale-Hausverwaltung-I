import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.rest.ressource.CustomerRessource;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

public class CustomerRessourceTest {

    @Test
    public void getCustomers() {
        CustomerRessource customerRessource = new CustomerRessource();

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        CustomerService customerService = new CustomerService(dbConnection);

        dbConnection.truncateAllTables();

        Customer customer = new Customer();
        customer.setFirstName("Marco");
        customer.setLastName("Poli");
        customer.setBirthDate(LocalDate.parse("1940-04-30"));
        customer.setGender(EGender.U);
        customerService.addCustomer(customer);

        JSONObject jsonObject = new JSONObject(customerRessource.getCustomer().getEntity().toString());

        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();
        Customer marco = customerJSONMapper.mapCustomer(new JSONObject(jsonObject.getJSONArray("customers").get(0)));

        assertEquals("Marco",marco.getFirstName());
    }

}
