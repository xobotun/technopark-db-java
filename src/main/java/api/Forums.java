package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Singleton
@Path("/forum")
public class Forums {

    @POST
    @Path("/create")
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
            return StandartAnswerManager.badRequest(errorList.toString());

        // Forum forum = new Forum(name, shortName, user);
        // return StandartAnswerManager.ok(forum.toJsonString())
        return StandartAnswerManager.ok();
    }

    @GET
    @Path("/details")
    public Response getDBStatus() {

        //return StandartAnswerManager.ok(forum.getDetails());
        return StandartAnswerManager.ok();
    }
}
