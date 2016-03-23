package helpers;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public static int create(String username, String about, String name, String email, boolean isAnonymous) throws Exception {
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
            if (rowsUpdated == 0)
                throw new Exception("5");
        }  catch (MySQLIntegrityConstraintViolationException ex) {
            throw new Exception("5");
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
    public static JSONObject getDetails(String email) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONObject result = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM User WHERE email=?");
            statement.setString(1, email);
            ResultSet rows = statement.executeQuery();
            if (!rows.first())
                throw new Exception("1");
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

    @Nullable
    public static JSONArray getUsersRelatedToForum(String forumShortName, boolean isDesc, String since, String limit) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT User.id, User.email, User.username, User.about, User.isAnonymous, User.name, Forum.shortName FROM Forum JOIN User ON User.email=Forum.user WHERE Forum.shortName=?");
            if (since != null)
                query.append(" AND id > " + since);
            if (isDesc)
                query.append(" ORDER BY name DESC");
            else
                query.append(" ORDER BY name ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, forumShortName);
            ResultSet rows = statement.executeQuery();
            while (rows.next()) {
                JSONObject temp = translate(rows);
                result.put(temp);
            }
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
