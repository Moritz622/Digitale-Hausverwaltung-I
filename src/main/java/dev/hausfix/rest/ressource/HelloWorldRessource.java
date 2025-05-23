package dev.hausfix.rest.ressource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("rest/world")
public class HelloWorldRessource {
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHelloWorld(){
        return "Hello World!";
    }
}
