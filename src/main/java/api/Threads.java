package api;

import helpers.Forum;
import helpers.Post;
import helpers.SubscriptionMap;
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

        int id;
        JSONObject thread;
        try {
             id = Thread.create(isDeleted, forum, title, isClosed, user, date, message, slug);
             thread = Thread.getDetails(id, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
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

        if (request.getQueryString() == null) {
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
                        if (related.get(i).equals("thread"))
                            return StandartAnswerManager.code3();
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
                    if (parameter.equals("thread"))
                        return StandartAnswerManager.code3();
                }
            threadID = Integer.parseInt(request.getParameter("thread"));

        }

        JSONObject thread;
        try { thread = Thread.getDetails(threadID, isUserDataRequested, isForumDataRequested); }
        catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(thread);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/remove/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response remove(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
        }

        Integer thread = null;
        try {
            thread = Thread.remove(threadID);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/restore/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response restore(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
        }

        Integer thread = null;
        try {
            thread = Thread.restore(threadID);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/close/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response close(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
        }

        Integer thread = null;
        try {
            thread = Thread.close(threadID);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/open/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response open(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
        }

        Integer thread = null;
        try {
            thread = Thread.open(threadID);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/subscribe/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response subscribe(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;
        String user;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread", "user"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
                user = jsonRequest.getString("user");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
            user = request.getParameter("user");
        }

        JSONObject thread = null;
        try {
            SubscriptionMap.create(threadID, user);
            thread = Thread.getDetails(threadID, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread).put("user", user));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/unsubscribe/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unsubscribe(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;
        String user;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread", "user"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
                user = jsonRequest.getString("user");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
            user = request.getParameter("user");
        }

        JSONObject thread = null;
        try {
            SubscriptionMap.delete(threadID, user);
            thread = Thread.getDetails(threadID, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread).put("user", user));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/update/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;
        String message;
        String slug;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread", "message", "slug"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
                message = jsonRequest.getString("message");
                slug = jsonRequest.getString("slug");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
            message = request.getParameter("message");
            slug = request.getParameter("slug");
        }

        JSONObject thread = null;
        try {
            Thread.update(threadID, message, slug);
            thread = Thread.getDetails(threadID, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread));
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @POST
    @Path("/vote/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response vote(String jsonString, @Context HttpServletRequest request) {
        int threadID = -1;
        boolean isDislike = false;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread", "vote"});
            if (errorList == null) {
                threadID = jsonRequest.getInt("thread");
                Integer temp = jsonRequest.getInt("vote");
                if (temp == -1)
                    isDislike = true;
                else if (temp == 1)
                    isDislike = false;
                else
                    return StandartAnswerManager.code3();

            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            threadID = Integer.parseInt(request.getParameter("thread"));
        }

        JSONObject thread = null;
        try {
            Thread.vote(threadID, isDislike);
            thread = Thread.getDetails(threadID, false, false);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }
        if (thread != null)
            return StandartAnswerManager.ok(new JSONObject().put("thread", thread));
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
        String user = null;

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
                user = jsonRequest.getString("user");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            since = request.getParameter("since");

            shortName = request.getParameter("forum");
            user = request.getParameter("user");

        }

        if (user == null && shortName == null)
            return StandartAnswerManager.code3();
        if (user != null && shortName != null)
            return StandartAnswerManager.code3();


        JSONArray threads;
        try {
            if (shortName != null)
                threads = Thread.getThreadsRelatedToForum(shortName, false, false, isDesc, since, limit);
            else
                threads = Thread.getThreadsRelatedToUser(user, isDesc, since, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (threads != null)
            return StandartAnswerManager.ok(threads);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }

    @GET
    @Path("/listPosts/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listPosts(String jsonString, @Context HttpServletRequest request) {
        boolean isDesc = true;
        String limit = null;
        String since = null;
        String sort = "flat";
        Integer thread = null;

        if (request.getQueryString() == null) {
            final JSONObject jsonRequest;
            try {
                jsonRequest = new JSONObject(jsonString);
            } catch (JSONException ex) {
                return StandartAnswerManager.badRequest();
            }
            final JSONArray errorList = StandartAnswerManager.showFieldsNotPresent(jsonRequest, new String[]{"thread"});
            if (errorList == null) {
                if (jsonRequest.has("order"))
                    if (jsonRequest.get("order").equals("asc"))
                        isDesc = false;
                if (jsonRequest.has("limit"))
                    limit = jsonRequest.getString("limit");
                if (jsonRequest.has("since"))
                    since = jsonRequest.getString("since");
                thread  = jsonRequest.getInt("thread");
                sort = jsonRequest.getString("sort");
            } else
                return StandartAnswerManager.badRequest(errorList);

        } else {
            if (request.getParameter("order").equals("asc"))
                isDesc = false;
            limit = request.getParameter("limit");
            since = request.getParameter("since");

            thread = Integer.parseInt(request.getParameter("thread"));
            sort = request.getParameter("sort");

        }

        if (sort == null)
            sort = "flat";

        if (!(sort.equals("flat") || sort.equals("tree") || sort.equals("parent_tree")))
            return StandartAnswerManager.code3();

        JSONArray posts;
        try {
            posts = Post.getPostsRelatedToThread(thread, sort, isDesc, since, limit);
        } catch (Exception ex) {
            return StandartAnswerManager.handleExceptions(ex);
        }

        if (posts != null)
            return StandartAnswerManager.ok(posts);
        else
            return StandartAnswerManager.badRequest("No such forum!");
    }
}
