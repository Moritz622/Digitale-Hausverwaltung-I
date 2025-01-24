import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.util.Helper;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;

public class CustomerJSONMapperTest {

    private static Helper helper;

    @BeforeAll
    public static void init(){
        helper = new Helper();
    }

    @Test
    public void EntityToJSON(){
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        Customer noID = new Customer();
        noID.setGender(EGender.W);
        noID.setLastName("Markart");
        noID.setBirthDate(LocalDate.now());
        noID.setFirstName("Nina");
        noID.setId(null);

        JSONObject jsonObject1 = customerJSONMapper.mapCustomer(helper.getCustomer()).getJSONObject("customer");
        JSONObject jsonObject2 = customerJSONMapper.mapCustomer(noID).getJSONObject("customer");

        assertEquals(helper.customerUUID.toString(), jsonObject1.getString("id"));
        assertEquals(noID.getLastName(), jsonObject2.getString("lastName"));
    }

    @Test
    public void JSONToEntity(){
        CustomerJSONMapper customerJSONMapper = new CustomerJSONMapper();

        JSONObject jsonObject1 = customerJSONMapper.mapCustomer(helper.getCustomer()).getJSONObject("customer");
        JSONObject jsonObject2 = customerJSONMapper.mapCustomer(helper.getCustomer()).getJSONObject("customer");

        jsonObject2.remove("id");

        Customer customer1 = customerJSONMapper.mapCustomer(jsonObject1);
        Customer customer2 = customerJSONMapper.mapCustomer(jsonObject2);

        assertEquals(customer1.getId().toString(), helper.customerUUID.toString());
        assertEquals(customer2.getLastName(), helper.customer.getLastName());
    }

}
