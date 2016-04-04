package helpers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;

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
            updateTreePath(parent, thread, newID);
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

    private static void updateTreePath(Integer parent, int thread, int self) {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;

        try {
            String rootPath;
            if (parent == null) {
                // If no parent specified
                statement = connection.prepareStatement("SELECT treePath FROM Post WHERE thread=? AND id!=? ORDER BY treePath DESC LIMIT 1");
                statement.setInt(1, thread);
                statement.setInt(2, self);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.first()) {
                    // If at least one another post is present
                    rootPath = resultSet.getString(1);
                    if (rootPath.indexOf('.') > 0)
                        rootPath = rootPath.substring(0, rootPath.indexOf('.'));
                    rootPath = ("000000" + String.valueOf(Integer.parseInt(rootPath) + 1)).substring(String.valueOf(Integer.parseInt(rootPath) + 1).length());

                }
                else
                    rootPath = "000001";
            } else  {
                // If parent is specified
                statement = connection.prepareStatement("SELECT treePath FROM Post WHERE thread=? AND parent=? AND id!=? ORDER BY treePath DESC LIMIT 1");
                statement.setInt(1, thread);
                statement.setInt(2, parent);
                statement.setInt(3, self);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.first()) {
                    // If parent has childs
                    rootPath = resultSet.getString(1);
                    String thisChildID = String.valueOf(Integer.parseInt(rootPath.substring(rootPath.lastIndexOf('.') + 1)) + 1);
                    rootPath = rootPath.substring(0, rootPath.lastIndexOf('.') + 1) + ("000000" + thisChildID).substring(thisChildID.length());
                }
                else {
                    // This is the fisrt child
                    statement = connection.prepareStatement("SELECT treePath FROM Post WHERE thread=? AND id=?");
                    statement.setInt(1, thread);
                    statement.setInt(2, parent);
                    resultSet = statement.executeQuery();
                    if (resultSet.first()) {
                        // Get parent data
                        rootPath = resultSet.getString(1);
                        rootPath += ".000001";
                    } else
                        rootPath = "???";
                }
            }
            statement = connection.prepareStatement("UPDATE Post SET treePath=? WHERE id=?");
            statement.setString(1, rootPath);
            statement.setInt(2, self);
            statement.executeUpdate();
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
                query.append(" AND `date` >= \"" + since + "\"");
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
                query.append(" AND `date` >= \"" + since + "\"");
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
    public static JSONArray getPostsRelatedToUser(String user, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE `user`=? AND isDeleted=0");
            if (since != null)
                query.append(" AND `date` >= \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY `date` DESC");
            else
                query.append(" ORDER BY `date` ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, user);
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


    @Nullable
    public static int getPostsCountRelatedToThread(int threadID)  {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        int result = 0;

        try {
            statement = connection.prepareStatement("SELECT COUNT(*) FROM Post WHERE thread=? AND isDeleted=0");
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

    @Nullable
    public static JSONArray getPostsRelatedToThread(int threadID, String sort, boolean isDesc, String since, String limit) throws Exception {
        if (sort.equals("tree"))
            return treeSort(threadID, isDesc, since, limit);

        if (sort.equals("parent_tree"))
            return parentTreeSort(threadID, isDesc, since, limit);

        return flatSort(threadID, isDesc, since, limit);
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


    private static JSONArray flatSort(int threadID, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE thread=?");
            if (since != null)
                query.append(" AND `date` >= \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY `date` DESC");
            else
                query.append(" ORDER BY `date` ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, threadID);
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

    private static JSONArray treeSort(int threadID, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE thread=?");
            if (since != null)
                query.append(" AND `date` >= \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY SUBSTR(treePath, 1, 6) DESC, treePath ASC");
            else
                query.append(" ORDER BY SUBSTR(treePath, 1, 6) ASC, treePath ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, threadID);
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

    private static JSONArray parentTreeSort(int threadID, boolean isDesc, String since, String limit) throws Exception {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        PreparedStatement statement = null;
        JSONArray result = new JSONArray();

        try {
            JSONArray rootResult = new JSONArray();
            StringBuilder query = new StringBuilder("SELECT * FROM Post WHERE thread=? AND parent IS NULL");
            if (since != null)
                query.append(" AND `date` >= \"" + since + "\"");
            if (isDesc)
                query.append(" ORDER BY treePath ASC");
            else
                query.append(" ORDER BY treePath ASC");
            if (limit != null)
                query.append(" LIMIT ").append(Integer.parseInt(limit));
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, threadID);
            ResultSet rows = statement.executeQuery();
            HashMap<Integer, String> treePaths = new HashMap<>();

            while (rows.next()) {
                JSONObject temp = translate(rows);
                treePaths.put(temp.getInt("id"), rows.getString("treePath"));
                rootResult.put(temp);
            }

            for (int i = 0; i < rootResult.length(); ++i)
            {
                result.put(rootResult.get(i));
                statement = connection.prepareStatement("SELECT * FROM Post WHERE thread=? AND parent IS NOT NULL AND treePath LIKE ?");
                statement.setInt(1, threadID);
                statement.setString(2, treePaths.get(((JSONObject)rootResult.get(i)).getInt("id")) + '%');
                rows = statement.executeQuery();
                while (rows.next()) {
                    JSONObject temp = translate(rows);
                    result.put(temp);
                }
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
}
