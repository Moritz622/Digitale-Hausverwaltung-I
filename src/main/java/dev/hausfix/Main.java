package dev.hausfix;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        final String pack = "dev.hausfix.rest.ressource";
        String url = "http://localhost:8069/rest";
        System.out.println("Start server"); System.out.println(url);
        final ResourceConfig rc = new ResourceConfig().packages(pack);
        final HttpServer server = JdkHttpServerFactory.createHttpServer( URI.create(url), rc);
        System.out.println("Ready for Requests....");
    }
}