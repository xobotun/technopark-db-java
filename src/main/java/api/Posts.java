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
        JSONObject post;
        try {
            post = Post.getDetails(id, false, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
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

        if (request.getQueryString() == null) {
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

        JSONObject post;
        try {
            post = Post.getDetails(postID, isUserDataRequested, isForumDataRequested, isThreadDataRequested);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (post != null)
            return StandartAnswerManager.ok(post);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/list/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list(String jsonString, @Context HttpServletRequest request) {
        boolean isDesc = true;
        String limit = null;
        String since = null;
        String shortName = null;
        String threadID = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{});
            if (errorList == null) {
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since = jsonRequest.getString("since");
                shortName = jsonRequest.getString("forum");
                threadID = jsonRequest.getString("thread");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            since = request.getParameter("since");

            shortName = request.getParameter("forum");
            threadID = request.getParameter("thread");

        }

        if (threadID == null &&  shortName == null)
            return StandartAnswerManager.code3();
        if (threadID != null && shortName != null)
            return StandartAnswerManager.code3();


        JSONArray posts;
        try {
            if (shortName != null)
                posts = Post.getPostsRelatedToForum(shortName, false, false, false, isDesc, since, limit);
            else
                posts = Post.getPostsRelatedToThread(threadID, false, false, false, isDesc, since, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (posts != null)
            return StandartAnswerManager.ok(posts);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/remove/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response remove(String jsonString, @Context HttpServletRequest request) {
        int postID = -1;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"post"});
            if (errorList == null) {
                postID = jsonRequest.getInt("post");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            postID = Integer.parseInt(request.getParameter("post"));
        }

        Integer post = null;
        try {
            post = Post.remove(postID);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (post != null)
            return StandartAnswerManager.ok(new JSONObject().put("post", post));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/restore/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response restore(String jsonString, @Context HttpServletRequest request) {
        int postID = -1;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"post"});
            if (errorList == null) {
                postID = jsonRequest.getInt("post");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            postID = Integer.parseInt(request.getParameter("post"));
        }

        Integer post = null;
        try {
            post = Post.restore(postID);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (post != null)
            return StandartAnswerManager.ok(new JSONObject().put("post", post));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/update/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(String jsonString, @Context HttpServletRequest request) {
        int postID = -1;
        String message;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"post", "message"});
            if (errorList == null) {
                postID = jsonRequest.getInt("post");
                message = jsonRequest.getString("message");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            postID = Integer.parseInt(request.getParameter("post"));
            message = request.getParameter("message");
        }

        JSONObject post = null;
        try {
            Post.update(postID, message);
            post = Post.getDetails(postID, false, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (post != null)
            return StandartAnswerManager.ok(new JSONObject().put("post", post));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/vote/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response vote(String jsonString, @Context HttpServletRequest request) {
        int postID = -1;
        boolean isDislike = false;
        String message;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"post", "vote"});
            if (errorList == null) {
                postID = jsonRequest.getInt("post");
                Integer temp = jsonRequest.getInt("vote");
                if (temp == -1)
                    isDislike = true;
                else if (temp == 1)
                    isDislike = false;
                else
                    return  StandartAnswerManager.code3();

            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            postID = Integer.parseInt(request.getParameter("post"));
            message = request.getParameter("message");
        }

        JSONObject post = null;
        try {
            Post.vote(postID, isDislike);
            post = Post.getDetails(postID, false, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (post != null)
            return StandartAnswerManager.ok(new JSONObject().put("post", post));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

}
