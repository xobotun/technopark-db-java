package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class Post {
    public static int create(Integer parent, boolean isApproved, boolean isHighlighted, boolean isEdited, boolean isSpam, boolean isDeleted, String date, int thread, String message, String user, String forum) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("INSERT INTO Post (parent, isApproved, isHighlighted, isEdited, isSpam, isDeleted, `date`, thread, message, user, forum) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            if (parent == null)
                statement.setNull(1, Types.INTEGER);
            else
                statement.setInt(1, parent);
            statement.setBoolean(2, isApproved);
            statement.setBoolean(3, isHighlighted);
            statement.setBoolean(4, isEdited);
            statement.setBoolean(5, isSpam);
            statement.setBoolean(6, isDeleted);
            statement.setString(7, date);
            statement.setInt(8, thread);
            statement.setString(9, message);
            statement.setString(10, user);
            statement.setString(11, forum);
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
    public static JSONObject getDetails(int id, boolean shouldExpandUser, boolean shouldExpandForum, boolean shouldExpandThread) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONObject result = null;

        try {
            statement = connection.prepareStatement("SELECT * FROM Post WHERE id=?");
            statement.setInt(1, id);
            ResultSet rows = statement.executeQuery();
            if (!rows.first())
                throw new Exception("1");
            result = translate(rows);
            if (shouldExpandUser)
                result.put("user", User.getDetails(result.getString("user")));
            if (shouldExpandForum)
                result.put("forum", Forum.getDetails(result.getString("forum"), false));
            if (shouldExpandThread)
                result.put("thread", Thread.getDetails(result.getInt("thread"), false, false));
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
    public static JSONArray getPostsRelatedToThread(String threadID, boolean shouldExpandUser, boolean shouldExpandForum, boolean shouldExpandThread, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE thread=? AND isDeleted=0");
            if (since != null)
                query.append(" AND `date` > \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY `date` DESC");
            else
                query.append(" ORDER BY `date` ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, Integer.parseInt(threadID));
            ResultSet rows = statement.executeQuery();
            while (rows.next()) {
                JSONObject temp = translate(rows);
                if (shouldExpandUser)
                    temp.put("user", User.getDetails(temp.getString("user")));
                if (shouldExpandForum)
                    temp.put("forum", Forum.getDetails(temp.getString("forum"), false));
                if (shouldExpandThread)
                    temp.put("thread", Thread.getDetails(temp.getInt("thread"), false, false));
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
    public static JSONArray getPostsRelatedToForum(String shortName, boolean shouldExpandUser, boolean shouldExpandForum, boolean shouldExpandThread, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE forum=? AND isDeleted=0");
            if (since != null)
                query.append(" AND `date` > \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY `date` DESC");
            else
                query.append(" ORDER BY `date` ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, shortName);
            ResultSet rows = statement.executeQuery();
            while (rows.next()) {
                JSONObject temp = translate(rows);
                if (shouldExpandUser)
                    temp.put("user", User.getDetails(temp.getString("user")));
                if (shouldExpandForum)
                    temp.put("forum", Forum.getDetails(temp.getString("forum"), false));
                if (shouldExpandThread)
                    temp.put("thread", Thread.getDetails(temp.getInt("thread"), false, false));
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
    public static int getPostsCountRelatedToThread(int threadID)  {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int result = 0;

        try {
            statement = connection.prepareStatement("SELECT COUNT(*) FROM Post WHERE thread=?  AND isDeleted=0");
            statement.setInt(1, threadID);
            ResultSet rows = statement.executeQuery();
            rows.first();
            result = rows.getInt(1);
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
            Object parentID = set.getInt("parent");
            if (set.wasNull())
                parentID = JSONObject.NULL;
            return new JSONObject().put("date", set.getString("date")).put("dislikes", set.getInt("dislikes")).put("forum", set.getString("forum")).put("id", set.getInt("id"))
                    .put("isApproved", set.getBoolean("isApproved")).put("isDeleted", set.getBoolean("isDeleted")).put("isEdited", set.getBoolean("isEdited"))
                    .put("isHighlighted", set.getBoolean("isHighlighted")).put("isSpam", set.getBoolean("isSpam")).put("likes", set.getInt("likes")).put("message", set.getString("message"))
                    .put("parent", parentID).put("points", set.getInt("points")).put("thread", set.getInt("thread")).put("user", set.getString("user"));

        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        }
        return new JSONObject().put("error", "No such forum!");
    }

    public static int remove(int postID) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Post SET isDeleted=1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, postID);
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

    public static int restore(int postID) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Post SET isDeleted=0 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, postID);
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

    public static int update(int postID, String message) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            statement = connection.prepareStatement("UPDATE Post SET message=? WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, message);
            statement.setInt(2, postID);
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

    public static int vote(int postID, boolean isDislike) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int newID = 0;

        try {
            if (isDislike)
                statement = connection.prepareStatement("UPDATE Post SET points=points-1, dislikes=dislikes+1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            else
                statement = connection.prepareStatement("UPDATE Post SET points=points+1, likes=likes+1 WHERE id=? ", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, postID);
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
