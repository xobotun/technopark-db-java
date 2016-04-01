package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowMap {

    public static int create(String follower, String followee) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int rowsUpdated = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO FollowMap (follower, followee) VALUES(?, ?)");
            statement.setString(1, follower);
            statement.setString(2, followee);
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

    public static int delete(String follower, String followee) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int rowsUpdated = 0;

        try {
            statement = connection.prepareStatement("DELETE FROM FollowMap WHERE follower=? AND followee=?");
            statement.setString(1, follower);
            statement.setString(2, followee);
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

    public static JSONArray getFollowers(String followee) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = null;

        try {
            statement = connection.prepareStatement("SELECT follower FROM FollowMap WHERE followee=?");
            statement.setString(1, followee);
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

    @Nullable
    public static JSONArray getFollowers(String user, boolean isDesc, Integer since_id, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT follower FROM FollowMap JOIN User ON followee=?");
            if (since_id != null)
                query.append(" WHERE id >= \"" + since_id + "\"");
            if (isDesc)
                query.append(" ORDER BY name DESC");
            else
                query.append(" ORDER BY name ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, user);
            ResultSet rows = statement.executeQuery();
            while (rows.next()) {
                result = translate(rows);
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

    public static JSONArray getFollowees(String follower) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = null;

        try {
            statement = connection.prepareStatement("SELECT followee FROM FollowMap WHERE follower=?");
            statement.setString(1, follower);
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

    @Nullable
    public static JSONArray getFollowees(String user, boolean isDesc, Integer since_id, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT followee FROM FollowMap JOIN User ON follower=?");
            if (since_id != null)
                query.append(" WHERE id >= \"" + since_id + "\"");
            if (isDesc)
                query.append(" ORDER BY name DESC");
            else
                query.append(" ORDER BY name ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, user);
            ResultSet rows = statement.executeQuery();
            while (rows.next()) {
                result = translate(rows);
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

    public static JSONArray translate(ResultSet set) {
        try {
            JSONArray result = new JSONArray();
            while (set.next())
                result.put(set.getString(1));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONArray();
    }
}
