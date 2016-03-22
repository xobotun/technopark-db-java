package helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowMap {

    public static int create(int follower, int followee) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int rowsUpdated = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO FollowMap (follower, followee) VALUES(?, ?)");
            statement.setInt(1, follower);
            statement.setInt(2, followee);
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

    public static JSONArray getFollowers(int followee) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = null;

        try {
            statement = connection.prepareStatement("SELECT follower FROM FollowMap WHERE followee=?");
            statement.setInt(1, followee);
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

    public static JSONArray getFollowees(int follower) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = null;

        try {
            statement = connection.prepareStatement("SELECT followee FROM FollowMap WHERE follower=?");
            statement.setInt(1, follower);
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

    public static JSONArray translate(ResultSet set) {
        try {
            JSONArray result = new JSONArray();
            while (set.next())
                result.put(set.getInt(1));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONArray();
    }
}
