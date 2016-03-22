package api;

import helpers.DBConnectionManager;
import helpers.TableCreator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;

@Singleton
@Path("/")
public class General {

    @POST
    @Path("clear/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearAllDB(String jsonString){
        if (jsonString.equals("{}"))
        {
            TableCreator.createAll();
            return StandartAnswerManager.ok();
        }
        else return StandartAnswerManager.badRequest();
    }

    @GET
    @Path("status/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDBStatus() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        JSONObject result = null;
        try {
            statement = connection.createStatement();
            ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM User");
            rows.first();
            int userCount = rows.getInt(1);

            rows = statement.executeQuery("SELECT COUNT(*) FROM Thread");
            rows.first();
            int threadCount = rows.getInt(1);

            rows = statement.executeQuery("SELECT COUNT(*) FROM Forum");
            rows.first();
            int forumCount = rows.getInt(1);

            rows = statement.executeQuery("SELECT COUNT(*) FROM User");
            rows.first();
            int postCount = rows.getInt(1);

            result = new JSONObject().put("user", userCount).put("thread", threadCount).put("forum", forumCount).put("post", postCount);
        } catch (SQLException ex) {
            result = new JSONObject().put("error", "Baka!");
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException ex) {
                    DBConnectionManager.printSQLExceptionData(ex);
                }
        }

        return StandartAnswerManager.ok(result);
    }
}
