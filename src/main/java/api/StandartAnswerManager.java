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

    public static Response ok() {
        return ok("OK");
    }

    public static Response badRequest(String s) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("code", "???").put("response", s).toString()).build();
    }

    public static Response badRequest(JSONArray json) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new JSONObject().put("code", 1).put("response", json).toString()).build();
    }


    public static Response badRequest() {
        return badRequest("The request was different from the one was expected.");
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
