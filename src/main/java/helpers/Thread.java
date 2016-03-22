package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.sql.*;

public class Thread {
    public static int create(boolean isDeleted, String forum, String title, boolean isClosed, String user, String date, String message, String slug) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO Thread (isDeleted, forum, title, isClosed, user, date, message, slug) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setBoolean(1, isDeleted);
            statement.setString(2, forum);
            statement.setString(3, title);
            statement.setBoolean(4, isClosed);
            statement.setString(5, user);
            statement.setString(6, date);
            statement.setString(7, message);
            statement.setString(8, slug);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            newID = resultSet.getInt(1);
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

        return newID;
    }

    @Nullable
    public static JSONObject getDetails(int id, boolean shouldExpandUser, boolean shouldExpandForum) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONObject result = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM Thread WHERE id=?");
            statement.setInt(1, id);
            ResultSet rows = statement.executeQuery();
            rows.first();
            result = translate(rows);
            if (shouldExpandUser)
                result.put("user", User.getDetails(result.getString("user")));
            if (shouldExpandForum)
                result.put("forum", Forum.getDetails(result.getString("forum"), false));
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
            return new JSONObject().put("date", set.getString("date")).put("dislikes", set.getInt("dislikes")).put("forum", set.getString("forum")).put("id", set.getInt("id"))
                    .put("isClosed", set.getBoolean("isClosed")).put("isDeleted", set.getBoolean("isDeleted")).put("likes", set.getInt("likes")).put("message", set.getString("message"))
                    .put("points", set.getInt("points")).put("posts", Post.getPostsCountRelatedToThread(set.getInt("id"))).put("slug", set.getString("slug")).put("title", set.getString("title")).put("user", set.getString("user"));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONObject().put("error", "No such forum!");
    }
}
