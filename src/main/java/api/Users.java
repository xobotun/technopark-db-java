package api;

import helpers.FollowMap;
import helpers.Forum;
import helpers.User;
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
@Path("/user")
public class Users {

    @POST
    @Path("/create/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewUser(String jsonString) {
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
        } catch (Exception ex) {
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

    @POST
    @Path("/unfollow/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unfollow(String jsonString, @Context HttpServletRequest request) {
        String follower = null;
        String followee = null;
        String message;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"follower", "followee"});
            if (errorList == null) {
                followee = jsonRequest.getString("followee");
                follower = jsonRequest.getString("follower");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            followee = request.getParameter("followee");
            follower = request.getParameter("follower");
        }

        JSONObject user = null;
        try {
            FollowMap.delete(follower, followee);
            user = User.getDetails(follower);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (user != null)
            return StandartAnswerManager.ok(user);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/follow/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response follow(String jsonString, @Context HttpServletRequest request) {
        String follower = null;
        String followee = null;
        String message;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"follower", "followee"});
            if (errorList == null) {
                followee = jsonRequest.getString("followee");
                follower = jsonRequest.getString("follower");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            followee = request.getParameter("followee");
            follower = request.getParameter("follower");
        }

        JSONObject user = null;
        try {
            FollowMap.create(follower, followee);
            user = User.getDetails(follower);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (user != null)
            return StandartAnswerManager.ok(user);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/updateProfile/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(String jsonString) {
        final JSONObject jsonRequest;

        try {
            jsonRequest = new JSONObject(jsonString);
        } catch (JSONException ex) {
            return StandartAnswerManager.badRequest();
        }

        String about;
        String name;
        String email;
        final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"about", "name", "user"});
        if (errorList == null) {
            about = jsonRequest.get("about").toString();
            name = jsonRequest.get("name").toString();
            email = jsonRequest.get("user").toString();
        } else
            return StandartAnswerManager.badRequest(errorList);

        JSONObject user;
        try {
            User.updateProfile(about, name, email);
            user = User.getDetails(email);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (user != null)
            return StandartAnswerManager.ok(user);
        else
            return StandartAnswerManager.badRequest("User already exists!");
    }

    @GET
    @Path("/listPosts/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listPosts(String jsonString, @Context HttpServletRequest request) {
        boolean isDesc = true;
        String user = null;
        String limit = null;
        String since = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"user"});
            if (errorList == null) {
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since = jsonRequest.getString("since");
                user = jsonRequest.getString("user");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            since = request.getParameter("since");

            user = request.getParameter("user");

        }
        JSONArray posts;
        try {
            posts = User.listPosts(user, isDesc, since, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (posts != null)
            return StandartAnswerManager.ok(posts);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/listFollowers/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listFollowers(String jsonString, @Context HttpServletRequest request) {
        boolean isDesc = true;
        String user = null;
        String limit = null;
        Integer since_id = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"user"});
            if (errorList == null) {
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since_id = jsonRequest.getInt("since_id");
                user = jsonRequest.getString("user");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            if (request.getParameter("since_id") != null)
                since_id = Integer.parseInt(request.getParameter("since_id"));

            user = request.getParameter("user");

        }
        JSONArray posts;
        try {
            posts = User.listFollowers(user, isDesc, since_id, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (posts != null)
            return StandartAnswerManager.ok(posts);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/listFollowing/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listFollowees(String jsonString, @Context HttpServletRequest request) {
        boolean isDesc = true;
        String user = null;
        String limit = null;
        Integer since_id = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"user"});
            if (errorList == null) {
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since_id = jsonRequest.getInt("since_id");
                user = jsonRequest.getString("user");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            if (request.getParameter("since_id") != null)
                since_id = Integer.parseInt(request.getParameter("since_id"));

            user = request.getParameter("user");

        }
        JSONArray posts;
        try {
            posts = User.listFollowees(user, isDesc, since_id, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (posts != null)
            return StandartAnswerManager.ok(posts);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}