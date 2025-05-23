package dev.hausfix.rest.ressource;

import dev.hausfix.entities.User;
import dev.hausfix.exceptions.DuplicateEntryException;
import dev.hausfix.exceptions.IncompleteDatasetException;
import dev.hausfix.exceptions.NoEntityFoundException;
import dev.hausfix.rest.objects.UserJSONMapper;
import dev.hausfix.rest.schema.SchemaLoader;
import dev.hausfix.rest.webtoken.JWTUtil;
import dev.hausfix.services.UserService;
import dev.hausfix.services.ReadingService;
import dev.hausfix.sql.DatabaseConnection;
import dev.hausfix.util.PropertyLoader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.attribute.standard.Media;
import java.util.UUID;

@Path("rest/users")
public class UserRessource {

    @POST
    @Path("/register")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response registerUser(String jsonString){
        UserJSONMapper userJSONMapper = new UserJSONMapper();

        User user = userJSONMapper.mapUser(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        UserService userService = new UserService(dbConnection);

        try {
            if(userService.addUser(user)) {
                return Response.status(201, "Created").entity(SchemaLoader.load(userJSONMapper.mapUser(user), "schema/UserJsonSchema.json").toString()).build();
            }
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400, "Bad Request: " + e.getMessage()).entity(null).build();
        }

        return Response.status(400, "Bad Request").build();
    }

    @DELETE
    @Path("/{uuid}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response deleteUsers(@PathParam("uuid") String uuid) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        UserService userService = new UserService(dbConnection);

        UserJSONMapper userJSONMapper = new UserJSONMapper();

        try {
            JSONObject userJSON = new JSONObject();

            User user = userService.getUser(UUID.fromString(uuid));

            userJSON = (JSONObject) SchemaLoader.load(userJSONMapper.mapUser(user), "schema/UserJsonSchema.json").get("user");

            userService.removeUser(user);

            return Response.status(200, "Ok").entity(userJSON.toString()).build();
        } catch (NoEntityFoundException e) {
            return Response.status(404).entity("Not Found").build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response addUser(String jsonString) {
        UserJSONMapper userJSONMapper = new UserJSONMapper();

        User user = userJSONMapper.mapUser(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        UserService userService = new UserService(dbConnection);

        try {
            if(userService.addUser(user)) {
                return Response.status(201, "Created").entity(SchemaLoader.load(userJSONMapper.mapUser(user), "schema/UserJsonSchema.json").toString()).build();
            }
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400, "Bad Request: " + e.getMessage()).entity(null).build();
        }

        return Response.status(400, "Bad Request").entity(null).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.TEXT_PLAIN)
    public Response putUser(String jsonString){
        UserJSONMapper userJSONMapper = new UserJSONMapper();

        User user = userJSONMapper.mapUser(new JSONObject(jsonString));

        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));
        UserService userService = new UserService(dbConnection);

        try {
            if(userService.getUser(user.getId()) != null){
                userService.updateUser(user);
            }else{
                return Response.status(404).entity("Not Found").build();
            }

            return Response.status(200).entity("Ok").build();
        } catch (NoEntityFoundException e) {
            return Response.status(404).entity("Not Found").build();
        } catch (IncompleteDatasetException | DuplicateEntryException e) {
            return Response.status(400).entity("Bad Request | " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Produces({MediaType.TEXT_PLAIN})
    public Response getUser(@PathParam("uuid") String uuid) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        UserService userService = new UserService(dbConnection);

        UserJSONMapper userJSONMapper = new UserJSONMapper();

        User user = null;

        try {
            user = userService.getUser(UUID.fromString(uuid));

            JSONObject userJSON = new JSONObject();

            userJSON = (JSONObject) SchemaLoader.load(userJSONMapper.mapUser(user), "schema/UserJsonSchema.json").get("user");

            return Response.status(200, "Ok").entity(userJSON.toString()).build();
        } catch (Exception e) {
            return Response.status(404).entity("Not Found").build();
        }
    }

    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        UserService userService = new UserService(dbConnection);

        UserJSONMapper userJSONMapper = new UserJSONMapper();

        User user = userJSONMapper.mapUser(new JSONObject(jsonString));

        if (userService.login(user)){
            try {
                UUID userID = userService.getUser(user.getUsername()).getId();

                String token = JWTUtil.generateToken(userID);

                return Response.status(200, "Ok").entity(new JSONObject().put("token", token).toString()).build();
            } catch (NoEntityFoundException e) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        else{
            return Response.status(400, "Not Authorized").build();
        }
    }

    @POST
    @Path("/isloggedin")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response isLoggedIn(String jsonString) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.openConnection(new PropertyLoader().getProperties("src/main/resources/hausfix.properties"));

        UserService userService = new UserService(dbConnection);

        UserJSONMapper userJSONMapper = new UserJSONMapper();

        JSONObject data = new JSONObject(jsonString);

        if(data.has("token")){
            UUID uuid = JWTUtil.validateToken(data.getString("token"));

            try {
                userService.getUser(uuid);

                return Response.status(Response.Status.OK).build();
            } catch (NoEntityFoundException e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }else{
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
