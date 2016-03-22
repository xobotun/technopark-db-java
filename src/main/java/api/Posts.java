package api;

import helpers.Forum;
import helpers.Post;
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
@Path("/post")
public class Posts {

    @POST
    @Path("/create/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewThread(String jsonString) {
        final JSONObject jsonRequest;

        try {
            jsonRequest = new JSONObject(jsonString);
        } catch (JSONException ex) {
            return StandartAnswerManager.badRequest();
        }

        Integer parent = null;
        boolean isApproved = false;
        boolean isHighlighted = false;
        boolean isEdited = false;
        boolean isSpam = false;
        boolean isDeleted = false;
        String date;
        int thread;
        String message;
        String user;
        String forum;
        final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"date", "thread", "message", "user", "forum"});
        if (errorList == null) {
            if (jsonRequest.has("parent") && !jsonRequest.isNull("parent"))
                parent = jsonRequest.getInt("parent");
            if (jsonRequest.has("isApproved"))
                isApproved = jsonRequest.getBoolean("isApproved");
            if (jsonRequest.has("isHighlighted"))
                isHighlighted = jsonRequest.getBoolean("isHighlighted");
            if (jsonRequest.has("isEdited"))
                isEdited = jsonRequest.getBoolean("isEdited");
            if (jsonRequest.has("isSpam"))
                isSpam = jsonRequest.getBoolean("isSpam");
            if (jsonRequest.has("isDeleted"))
                isDeleted = jsonRequest.getBoolean("isDeleted");
            date = jsonRequest.getString("date");
            thread = jsonRequest.getInt("thread");
            message = jsonRequest.getString("message");
            user = jsonRequest.getString("user");
            forum = jsonRequest.getString("forum");
        } else
            return StandartAnswerManager.badRequest(errorList);

        int id = Post.create(parent,isApproved,isHighlighted,isEdited,isSpam,isDeleted,date,thread,message,user,forum);
        JSONObject post = Post.getDetails(id, false, false, false);
        if (post != null)
            return StandartAnswerManager.ok(post);
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
        boolean isThreadDataRequested = false;
        int postID = -1;

        if (request.getQueryString().isEmpty()) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"post"});
            if (errorList == null) {
                if (jsonRequest.has("related")) {
                    JSONArray related = jsonRequest.getJSONArray("related");
                    for (int i = 0; i < related.length(); ++i) {
                        if (related.get(i).equals("user"))
                            isUserDataRequested = true;
                        if (related.get(i).equals("forum"))
                            isForumDataRequested = true;
                        if (related.get(i).equals("thread"))
                            isThreadDataRequested = true;
                    }

                }
                postID = jsonRequest.getInt("post");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("related") != null)
                for (String parameter : request.getParameterValues("related")) {
                    if (parameter.equals("user"))
                        isUserDataRequested = true;
                    if (parameter.equals("forum"))
                        isForumDataRequested = true;
                    if (parameter.equals("thread"))
                        isThreadDataRequested = true;
                }
            postID = Integer.parseInt(request.getParameter("post"));

        }

        JSONObject post = Post.getDetails(postID, isUserDataRequested, isForumDataRequested, isThreadDataRequested);
        if (post != null)
            return StandartAnswerManager.ok(post);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}
