package api;

import helpers.Forum;
import helpers.User;
import org.eclipse.jetty.server.Authentication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/user")
public class Users {

    @POST
    @Path("/create/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewUser(String jsonString){
        final JSONObject jsonRequest;

        try {
            jsonRequest = new JSONObject(jsonString);
        } catch (JSONException ex) {
            return StandartAnswerManager.badRequest();
        }

        String username;
        String about;
        String name;
        String email;
        boolean isAnonymous = false;
        final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"username", "about", "name", "email"});
        if (errorList == null) {
            username = jsonRequest.get("username").toString();
            about = jsonRequest.get("about").toString();
            name = jsonRequest.get("name").toString();
            email = jsonRequest.get("email").toString();
            if (jsonRequest.has("isAnonymous"))
                isAnonymous = jsonRequest.getBoolean("isAnonymous");
        } else
            return StandartAnswerManager.badRequest(errorList);

        JSONObject user;
        try {
            User.create(username, about, name, email, isAnonymous);
            user = User.getDetails(email);
        } catch ( Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (user != null)
            return StandartAnswerManager.ok(user);
        else
            return StandartAnswerManager.badRequest("User already exists!");
    }

    @GET
    @Path("/details/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDetails(String jsonString, @Context HttpServletRequest request) {
        String userEmail;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"forum"});
            if (errorList == null) {
                userEmail = jsonRequest.getString("forum");
            } else
                return StandartAnswerManager.badRequest(errorList);
        } else {
            userEmail = request.getParameter("user");
        }


        JSONObject user;
        try {
            user = User.getDetails(userEmail);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (user != null)
            return StandartAnswerManager.ok(user);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}
