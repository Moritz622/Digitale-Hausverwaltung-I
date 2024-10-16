package dev.hausfix;

import com.sun.net.httpserver.HttpServer;
import dev.hausfix.entities.Customer;
import dev.hausfix.enumerators.EGender;
import dev.hausfix.services.CustomerService;
import dev.hausfix.sql.DatabaseConnection;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.mariadb.jdbc.plugin.codec.LocalDateCodec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.Properties;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        final String pack = "dev.hausfix.rest.ressource";
        String url = "http://localhost:8069/rest";
        System.out.println("Start server"); System.out.println(url);
        final ResourceConfig rc = new ResourceConfig().packages(pack);
        final HttpServer server = JdkHttpServerFactory.createHttpServer( URI.create(url), rc);

        Properties properties = new Properties();

        try(InputStream input = new FileInputStream("src/main/resources/hausfix.properties")){
            properties.load(input);

            DatabaseConnection connection = (DatabaseConnection) new DatabaseConnection().openConnection(properties);
            connection.removeAllTables();
            connection.createAllTables();

            CustomerService customerService = new CustomerService(connection);

            Customer customer = new Customer();
            customer.setFirstName("Nina");
            customer.setLastName("Markart");
            customer.setGender(EGender.W);
            LocalDate test = new LocalDate(2005, 7, 20);
            customer.setBirthDate(test);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}