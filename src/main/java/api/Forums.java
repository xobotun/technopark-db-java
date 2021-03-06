package api;

import helpers.Forum;
import helpers.Post;
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
import java.util.Iterator;

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

        JSONObject forum;
        try {
            Forum.create(name, shortName, user);
            forum = Forum.getDetails(shortName, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (forum != null)
            return StandartAnswerManager.ok(forum);
        else
            return StandartAnswerManager.badRequest("Forum already exists!");
    }

    @GET
    @Path("/details/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetails(String jsonString, @Context HttpServletRequest request) {
        boolean isUserDataRequested = false;
        String forumShortName;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"forum"});
            if (errorList == null) {
                if (jsonRequest.has("related"))
                {
                    JSONArray related = jsonRequest.getJSONArray("related");
                    for (int i = 0; i < related.length(); ++i)
                        if (related.get(i).equals("user"))
                            isUserDataRequested = true;

                }
                forumShortName = jsonRequest.getString("forum");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("related") != null)
                isUserDataRequested = request.getParameter("related").equals("user");
            forumShortName = request.getParameter("forum");
        }


        JSONObject forum;
        try {
            forum = Forum.getDetails(forumShortName, isUserDataRequested);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (forum != null)
            return StandartAnswerManager.ok(forum);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/listPosts/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listPosts(String jsonString, @Context HttpServletRequest request) {
        boolean isUserDataRequested = false;
        boolean isForumDataRequested = false;
        boolean isThreadDataRequested = false;
        boolean isDesc = true;
        String shortName = null;
        String limit = null;
        String since = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"forum"});
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
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since = jsonRequest.getString("since");
                shortName = jsonRequest.getString("forum");
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
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            since = request.getParameter("since");

            shortName = request.getParameter("forum");

        }
        JSONArray posts;
        try{
            posts = Forum.listPosts(shortName, isUserDataRequested, isForumDataRequested, isThreadDataRequested, isDesc, since, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (posts != null)
            return StandartAnswerManager.ok(posts);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/listThreads/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listThreads(String jsonString, @Context HttpServletRequest request) {
        boolean isUserDataRequested = false;
        boolean isForumDataRequested = false;
        boolean isDesc = true;
        String shortName = null;
        String limit = null;
        String since = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"forum"});
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
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since = jsonRequest.getString("since");
                shortName = jsonRequest.getString("forum");
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
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            since = request.getParameter("since");

            shortName = request.getParameter("forum");

        }
        JSONArray threads;
        try {
            threads = Forum.listThreads(shortName, isUserDataRequested, isForumDataRequested, isDesc, since, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (threads != null)
            return StandartAnswerManager.ok(threads);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/listUsers/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listUsers(String jsonString, @Context HttpServletRequest request) {
        boolean isDesc = true;
        String shortName = null;
        String limit = null;
        String sinceID = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"forum"});
            if (errorList == null) {
                    if (jsonRequest.has("order"))
                        if (jsonRequest.get("order").equals("asc"))
                            isDesc = false;
                    if (jsonRequest.has("limit"))
                        limit = jsonRequest.getString("limit");
                    if (jsonRequest.has("since_id"))
                        sinceID = jsonRequest.getString("since");
                shortName = jsonRequest.getString("forum");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            sinceID = request.getParameter("since_id");
            shortName = request.getParameter("forum");

        }

        JSONArray users = Forum.listUsers(shortName, isDesc, sinceID, limit);
        if (users != null)
            return StandartAnswerManager.ok(users);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}
