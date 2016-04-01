package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class Thread {
    public static int create(boolean isDeleted, String forum, String title, boolean isClosed, String user, String date, String message, String slug) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO Thread (isDeleted, forum, title, isClosed, user, `date`, message, slug) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
    public static JSONObject getDetails(int id, boolean shouldExpandUser, boolean shouldExpandForum) throws Exception {
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

    @Nullable
    public static JSONArray getThreadsRelatedToForum(String forumShortName, boolean shouldExpandUser, boolean shouldExpandForum, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Thread WHERE forum=?  AND isDeleted=0");
            if (since != null)
                query.append(" AND `date` >= \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY `date` DESC");
            else
                query.append(" ORDER BY `date` ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, forumShortName);
            ResultSet rows = statement.executeQuery();
            while (rows.next()) {
                JSONObject temp = translate(rows);
                if (shouldExpandUser)
                    temp.put("user", User.getDetails(temp.getString("user")));
                if (shouldExpandForum)
                    temp.put("forum", Forum.getDetails(temp.getString("forum"), false));
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

    @Nullable
    public static JSONArray getThreadsRelatedToUser(String founderName, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Thread WHERE user=?  AND isDeleted=0");
            if (since != null)
                query.append(" AND `date` >= \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY `date` DESC");
            else
                query.append(" ORDER BY `date` ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, founderName);
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
            return new JSONObject().put("date", set.getString("date")).put("dislikes", set.getInt("dislikes")).put("forum", set.getString("forum")).put("id", set.getInt("id"))
                    .put("isClosed", set.getBoolean("isClosed")).put("isDeleted", set.getBoolean("isDeleted")).put("likes", set.getInt("likes")).put("message", set.getString("message"))
                    .put("points", set.getInt("points")).put("posts", Post.getPostsCountRelatedToThread(set.getInt("id"))).put("slug", set.getString("slug")).put("title", set.getString("title")).put("user", set.getString("user"));
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONObject().put("error", "No such forum!");
    }

    public static int open(int threadID) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Thread SET isClosed=0 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.first())
                throw new Exception("1");
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

    public static int close(int threadID) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Thread SET isClosed=1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.first())
                throw new Exception("1");
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

    public static int remove(int threadID) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Post SET isDeleted=1 WHERE thread=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE Thread SET isDeleted=1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.first())
                throw new Exception("1");
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

    public static int restore(int threadID) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Post SET isDeleted=0 WHERE thread=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE Thread SET isDeleted=0 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.first())
                throw new Exception("1");
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

    public static int update(int threadID, String message, String slug) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Thread SET message=?, slug=? WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, message);
            statement.setString(2, slug);
            statement.setInt(3, threadID);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.first())
                throw new Exception("1");
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

    public static int vote(int threadID, boolean isDislike) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            if (isDislike)
                statement = connection.prepareStatement("UPDATE Thread SET points=points-1, dislikes=dislikes+1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            else
                statement = connection.prepareStatement("UPDATE Thread SET points=points+1, likes=likes+1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, threadID);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (!resultSet.first())
                throw new Exception("1");
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
}
