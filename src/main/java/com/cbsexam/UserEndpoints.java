package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;

import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

    /**
     * @param idUser
     * @return Responses
     */
    @GET
    @Path("/{idUser}")
    public Response getUser(@PathParam("idUser") int idUser) {

        // Use the ID to get the user from the controller.
        User user = UserController.getUser(idUser);

        // TODO: Add Encryption to JSON - FIXED
        // Convert the user object to json in order to return the object
        String json = new Gson().toJson(user);
        //Her laver jeg krytpering
        json = Encryption.encryptDecryptXOR(json);

        // Return the user with the status code 200
        // TODO: What should happen if something breaks down? - FIXED
        if (user != null) {
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
            //så man ved det er sket en fejl
        } else {
            return Response.status(400).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
        }

    }

    // Gør at man kan hente klasse, ved at oprette et obejekt af Usercache - udenfor så man kan bruge den i andre klasse
    //Skal være static, da det kun skal hentes engang
    static UserCache userCache = new UserCache();

    /**
     * @return Responses
     */
    @GET
    @Path("/")
    public Response getUsers() {

        // Write to log that we are here
        Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

        // Get a list of users
        //Henter getUser metoden fra userCache
        ArrayList<User> users = userCache.getUsers(false);

        // TODO: Add Encryption to JSON - FIXED
        // Transfer users to json in order to return it to the user
        String json = new Gson().toJson(users);

        //Her laver jeg krytpering
        json = Encryption.encryptDecryptXOR(json);

        // Return the users with the status code 200
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {

        // Read the json from body and transfer it to a user class
        User newUser = new Gson().fromJson(body, User.class);

        // Use the controller to add the user
        User createUser = UserController.createUser(newUser);

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(createUser);
        //Her laver jeg krytpering
        json = Encryption.encryptDecryptXOR(json);

        // Return the data to the user
        if (createUser != null) {
            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
        } else {
            return Response.status(400).entity("Could not create user").build();
        }
    }

    // TODO: Make the system able to login users and assign them a token to use throughout the system. - FIXED
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String body) {

        //læser JSON fra body og transfer det til user klasse
        User user = new Gson().fromJson(body, User.class);

        //Henter user med det added ID og returner det til user
        String token = UserController.loginUser(user);

        /// Returner data til user
        if (token != "") {
            // Returner et response med status 200 og JSON som en type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
        } else {
            return Response.status(400).entity("Kunne ikke lave en User").build();
        }
    }

    @DELETE
    @Path("/delete")
    // TODO: Make the system able to delete users - FIXED
    public Response deleteUser(String body) {
        User user = new Gson().fromJson(body, User.class);

        if (UserController.deleteUser(user.getToken())) {
            return Response.status(200).entity("Bruger id er slettet fra siden").build();
        } else {
            // Return a response with status 200 and JSON as type
            return Response.status(400).entity("Bruger id kunne ikke findes").build();
        }
    }

    @POST
    @Path("/updateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    // TODO: Make the system able to update users - FIXED
    public Response updateUser(String body) {

        // Read the json from body and transfer it to a user class
        User user = new Gson().fromJson(body, User.class);

        if (UserController.updateUser(user, user.getToken())) {

            //opdaterer Cachen
            userCache.getUsers(true);

            // Return a response with status 200 and JSON as type
            return Response.status(200).entity("Bruger er updateret i systemet").build();
        } else {
        // Return a response with status 200 and JSON as type
        return Response.status(400).entity("Brugeren kan ikke findes i systemet").build();
    }
}

}


