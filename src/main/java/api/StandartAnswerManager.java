package api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;

public class StandartAnswerManager {

    public static Response ok(String s) {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 0).put("response", s).toString()).build();
    }

    public static Response ok(JSONObject json) {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 0).put("response", json).toString()).build();
    }

    public static Response ok(JSONArray json) {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 0).put("response", json).toString()).build();
    }

    public static Response ok() {
        return ok("OK");
    }

    public static Response badRequest(String s) {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 2).put("response", s).toString()).build();
    }

    public static Response badRequest(JSONArray json) {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 3).put("response", json).toString()).build();
    }

    public static Response badRequest() {
        return badRequest("The request was different from the one was expected.");
    }

    public static Response code1() {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 1).put("response", "BAKA!").toString()).build();
    }

    public static Response code5() {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 5).put("response", "BAKA!").toString()).build();
    }

    public static Response code3() {
        return Response.status(Response.Status.OK).entity(new JSONObject().put("code", 3).put("response", "BAKA!").toString()).build();
    }

    public static Response handleExceptions(Exception ex) {
        if (ex.getMessage().equals("1"))
            return StandartAnswerManager.code1();
        if (ex.getMessage().equals("5"))
            return StandartAnswerManager.code5();
        return StandartAnswerManager.badRequest();
    }

    @Nullable
    public static JSONArray showFieldsNotPresent(@NotNull JSONObject json, @NotNull String[] requiredFields) {
        JSONArray errorList = null;
        for (String field : requiredFields)
            if (!json.has(field)) {
                if (errorList == null)
                    errorList = new JSONArray();
                errorList.put(new JSONObject().put(field, "Field \"" + field + "\" not present!"));
            }

        return errorList;
    }
}
