package api;

import helpers.Forum;
import helpers.Thread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/thread")
public class Threads {

    @POST
    @Path("/create/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewThread(String jsonString){
        final JSONObject jsonRequest;

        try {
            jsonRequest = new JSONObject(jsonString);
        } catch (JSONException ex) {
            return StandartAnswerManager.badRequest();
        }

        boolean isDeleted = false;
        String forum;
        String title;
        boolean isClosed;
        String user;
        String date;
        String message;
        String slug;
        final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"forum", "title", "isClosed", "user", "date", "message", "slug"});
        if (errorList == null) {
            if (jsonRequest.has("isDeleted"))
                isDeleted = jsonRequest.getBoolean("isDeleted");
            forum = jsonRequest.getString("forum");
            title = jsonRequest.getString("title");
            isClosed = jsonRequest.getBoolean("isClosed");
            user = jsonRequest.getString("user");
            date = jsonRequest.getString("date");
            message = jsonRequest.getString("message");
            slug = jsonRequest.getString("slug");
        } else
            return StandartAnswerManager.badRequest(errorList);

        int id = Thread.create(isDeleted, forum, title, isClosed, user, date, message, slug);
        JSONObject thread = Thread.getDetails(id, false, false);
        if (thread != null)
            return StandartAnswerManager.ok(thread);
        else
            return StandartAnswerManager.badRequest("Forum already exists!");
    }

    @GET
    @Path("/details/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDetails(String jsonString, @Context HttpServletRequest request) {
        boolean isUserDataRequested = false;
        boolean isForumDataRequested = false;
        int threadID = -1;

        if (request.getQueryString().isEmpty()) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread"});
            if (errorList == null) {
                if (jsonRequest.has("related")) {
                    JSONArray related = jsonRequest.getJSONArray("related");
                    for (int i = 0; i < related.length(); ++i) {
                        if (related.get(i).equals("user"))
                            isUserDataRequested = true;
                        if (related.get(i).equals("forum"))
                            isForumDataRequested = true;
                    }

                }
                threadID = jsonRequest.getInt("thread");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("related") != null)
                for (String parameter : request.getParameterValues("related")) {
                    if (parameter.equals("user"))
                        isUserDataRequested = true;
                    if (parameter.equals("forum"))
                        isForumDataRequested = true;
                }
            threadID = Integer.parseInt(request.getParameter("thread"));

        }

        JSONObject thread = Thread.getDetails(threadID, isUserDataRequested, isForumDataRequested);
        if (thread != null)
            return StandartAnswerManager.ok(thread);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}
