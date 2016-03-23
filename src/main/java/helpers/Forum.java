package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Forum {
    public static int create(String name, String shortName, String email) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int rowsUpdated = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO Forum (name, shortName, user) VALUES(?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, shortName);
            statement.setString(3, email);
            rowsUpdated = statement.executeUpdate();
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException ex) {
                    DBConnectionManager.printSQLExceptionData(ex);
                }
        }

        return rowsUpdated;
    }

    @Nullable
    public static JSONObject getDetails(String shortName, boolean shouldExpandUser) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONObject result = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM Forum WHERE shortName=?");
            statement.setString(1, shortName);
            ResultSet rows = statement.executeQuery();
            result = translate(rows);
            if (shouldExpandUser)
                result.put("user", User.getDetails(result.getString("user")));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException ex) {
                    DBConnectionManager.printSQLExceptionData(ex);
                }
        }

        return result;
    }

    @Nullable
    public static JSONArray listPosts(String shortName, boolean shouldExpandUser, boolean shouldExpandForum, boolean shouldExpandThread, boolean isDesc, String since, String limit) throws  Exception {
        //int threadID = getDetails(shortName, false).getInt("id");
        return Post.getPostsRelatedToForum(shortName, shouldExpandUser, shouldExpandForum, shouldExpandThread, isDesc, since, limit);
    }

    @Nullable
    public static JSONArray listThreads(String shortName, boolean shouldExpandUser, boolean shouldExpandForum, boolean isDesc, String since, String limit) throws Exception {
        return Thread.getThreadsRelatedToForum(shortName, shouldExpandUser, shouldExpandForum, isDesc, since, limit);
    }

    @Nullable
    public static JSONArray listUsers(String shortName, boolean isDesc, String since, String limit) {
        return User.getUsersRelatedToForum(shortName, isDesc, since, limit);
    }

    public static JSONObject translate(ResultSet set) {
        try {
            if (set.first())
                return new JSONObject().put("id", set.getInt("id")).put("name", set.getString("name")).put("short_name", set.getString("shortName")).put("user", set.getString("user"));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONObject().put("error", "No such forum!");
    }
}
