package dev.hausfix.rest;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.File;
import java.net.URI;

public class Server {

    private static HttpServer server;

    public static void startServer(String url){
        String pack = "dev.hausfix.rest.ressource";
        System.out.println("Start server");
        System.out.println(url);

        ResourceConfig rc = new ResourceConfig()
                .packages(pack)
                .register(dev.hausfix.rest.CORSFilter.class)
                .register(StaticResourceHandler.class)
                .register(MultiPartFeature.class);

        server = JdkHttpServerFactory.createHttpServer(URI.create(url), rc);
    }

    public static void stopServer(){
        server.stop(420);
    }
}
