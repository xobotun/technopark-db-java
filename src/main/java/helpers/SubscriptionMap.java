package helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscriptionMap {

    public static int create(int threadID, int userID) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int rowsUpdated = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO SubscriptionMap (thread, user) VALUES(?, ?)");
            statement.setInt(1, threadID);
            statement.setInt(2, userID);
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

    public static JSONArray getThreads(int userID) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = null;

        try {
            statement = connection.prepareStatement("SELECT thread FROM SubscriptionMap WHERE user=?");
            statement.setInt(1, userID);
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

    public static JSONArray getSubscribers(int threadID) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = null;

        try {
            statement = connection.prepareStatement("SELECT user FROM SubscriptionMap WHERE thread=?)");
            statement.setInt(1, threadID);
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
