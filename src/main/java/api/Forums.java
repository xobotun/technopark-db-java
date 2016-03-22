package api;

import helpers.Forum;
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
@Path("/forum")
public class Forums {

    @POST
    @Path("/create/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewForum(String jsonString){
        final JSONObject jsonRequest;

        try {
            jsonRequest = new JSONObject(jsonString);
        } catch (JSONException ex) {
            return StandartAnswerManager.badRequest();
        }

        String name;
        String shortName;
        String user;
        final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"name", "short_name", "user"});
        if (errorList == null) {
            name = jsonRequest.get("name").toString();
            shortName = jsonRequest.get("short_name").toString();
            user = jsonRequest.get("user").toString();
        } else
            return StandartAnswerManager.badRequest(errorList);

        Forum.create(name, shortName, user);
        JSONObject forum = Forum.getDetails(shortName, false);
        if (forum != null)
            return StandartAnswerManager.ok(forum);
        else
            return StandartAnswerManager.badRequest("Forum already exists!");
    }

    @GET
    @Path("/details/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetails(@Context HttpServletRequest request) {
        boolean isUserDataRequested = request.getParameter("related").equals("user");
        String forumShortName = request.getParameter("forum");
        JSONObject forum = Forum.getDetails(forumShortName, isUserDataRequested);
        if (forum != null)
            return StandartAnswerManager.ok(forum);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}
