import dev.hausfix.entities.Customer;
import dev.hausfix.entities.User;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.CustomerJSONMapper;
import dev.hausfix.rest.objects.UserJSONMapper;
import dev.hausfix.rest.ressource.CustomerRessource;
import dev.hausfix.rest.ressource.UserRessource;
import dev.hausfix.services.CustomerService;
import dev.hausfix.services.UserService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.Helper;
import dev.hausfix.util.PropertyLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;

public class UserResourceTest {

    private static Helper helper;

    @BeforeAll
    public static void init(){
        helper = new Helper();
    }

    @Test
    public void addUser(){
        UserRessource userRessource = new UserRessource();
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        UserService userService = new UserService(dbConnection);
        dbConnection.truncateAllTables();

        User user = new User();
        user.setEmail("huanbock");
        user.setPassword("1234");
        user.setUserame("kek");

        UserJSONMapper userJSONMapper = new UserJSONMapper();

        userRessource.addUser(userJSONMapper.mapUser(user).get("user").toString());

        try {
            User marco = userService.getUser(user.getId());

            assertEquals(user.getEmail(), marco.getEmail());
        } catch (NoEntityFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
