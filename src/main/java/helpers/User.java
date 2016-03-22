package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public static int create(String username, String about, String name, String email, boolean isAnonymous) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int rowsUpdated = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO User (username, about, name, email, isAnonymous) VALUES(?, ?, ?, ? ,?)");
            statement.setString(1, username);
            statement.setString(2, about);
            statement.setString(3, name);
            statement.setString(4, email);
            statement.setBoolean(5, isAnonymous);
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
    public static JSONObject getDetails(String email) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONObject result = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM User WHERE email=?");
            statement.setString(1, email);
            ResultSet rows = statement.executeQuery();
            result = translate(rows);
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


    public static JSONObject translate(ResultSet set) {
        try {
            if (set.first())
                if (!set.getBoolean("isAnonymous"))
                    return new JSONObject().put("id", set.getInt("id")).put("about", set.getString("about")).put("email", set.getString("email")).put("name", set.getString("name"))
                        .put("username", set.getString("username")).put("isAnonymous", set.getBoolean("isAnonymous")).put("followers", FollowMap.getFollowers(set.getInt("id")))
                        .put("followees", FollowMap.getFollowees(set.getInt("id"))).put("subscriptions", SubscriptionMap.getThreads(set.getInt("id")));
                else
                    return new JSONObject().put("id", set.getInt("id")).put("about", JSONObject.NULL).put("email", set.getString("email")).put("name", JSONObject.NULL)
                        .put("username", JSONObject.NULL).put("isAnonymous", set.getBoolean("isAnonymous")).put("followers", FollowMap.getFollowers(set.getInt("id")))
                        .put("followees", FollowMap.getFollowees(set.getInt("id"))).put("subscriptions", SubscriptionMap.getThreads(set.getInt("id")));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONObject().put("error", "No such user!");
    }
}
