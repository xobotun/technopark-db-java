package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Singleton
@Path("/")
public class General {

    @POST
    @Path("/clear")
    public Response clearAllDB(String jsonString){
        if (jsonString == "{}")
        {
            // Drop everyhting
            return StandartAnswerManager.ok();
        }
        else return StandartAnswerManager.badRequest();
    }

    @GET
    @Path("/status")
    public Response getDBStatus() {
        // Gather counts
        int userCount = 0;
        int threadCount = 0;
        int forumCount = 0;
        int postCount = 0;

        return StandartAnswerManager.ok(new JSONObject().put("user", userCount).put("thread", threadCount).put("forum", forumCount).put("post", postCount).toString());
    }
}
