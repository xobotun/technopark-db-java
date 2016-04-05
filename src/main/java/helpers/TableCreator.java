package helpers;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator {
    public static void createAll() {
        createDB();
        createForum();
        createUser();
        createFollowMap();
        createPost();
        createThread();
        createSubscriptionMap();
    }

    public static void dropAll() {
        Connection connection = DBConnectionManager.getInstance().getRootConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP USER IF EXISTS " + DBConnectionManager.LOGIN_DOMAIN);
            statement.execute("DROP DATABASE IF EXISTS " + DBConnectionManager.DBNAME + ";");

            System.out.println("Database succesfully dropped!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createDB() {
        Connection connection = DBConnectionManager.getInstance().getRootConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();

            statement.execute("SET NAMES 'utf8';");
            statement.execute("DROP USER IF EXISTS " + DBConnectionManager.LOGIN_DOMAIN);
            statement.execute("CREATE USER " + DBConnectionManager.LOGIN_DOMAIN + " IDENTIFIED BY \"" + DBConnectionManager.PASSWORD + "\";");

            //statement.execute("DROP DATABASE IF EXISTS " + DBConnectionManager.DBNAME + ";");
            statement.execute("CREATE DATABASE IF NOT EXISTS " + DBConnectionManager.DBNAME + " character set utf8;");

            statement.execute("GRANT ALL ON " + DBConnectionManager.DBNAME + ".* TO " + DBConnectionManager.LOGIN_DOMAIN + ";");

            System.out.println("Database succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createForum() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + DBConnectionManager.DBNAME + ".Forum;");
            statement.execute("CREATE TABLE IF NOT EXISTS " + DBConnectionManager.DBNAME + ".Forum (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "shortName VARCHAR(255) NOT NULL, " +
                    "`user` VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (id));");
            System.out.println("Forum succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);;
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createUser() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + DBConnectionManager.DBNAME + ".User;");
            statement.execute("CREATE TABLE IF NOT EXISTS " + DBConnectionManager.DBNAME + ".User (" +
                              "id INT NOT NULL AUTO_INCREMENT, " +
                              "email VARCHAR(255) NOT NULL, " +
                              "username VARCHAR(255) NULL, " +
                              "about TEXT NULL, " +
                              "isAnonymous TINYINT NOT NULL, " +
                              "name VARCHAR(255) NULL, " +
                              "PRIMARY KEY (email)," +
                              "INDEX user_id (id ASC));");
            System.out.println("User succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createFollowMap() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + DBConnectionManager.DBNAME + ".FollowMap;");
            statement.execute("CREATE TABLE IF NOT EXISTS " + DBConnectionManager.DBNAME + ".FollowMap (" +
                    "follower VARCHAR(255) NOT NULL, " +
                    "followee VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (follower, followee));");
            System.out.println("FollowMap succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createPost() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + DBConnectionManager.DBNAME + ".Post;");
            statement.execute("CREATE TABLE IF NOT EXISTS " + DBConnectionManager.DBNAME + ".Post (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "parent INT NULL DEFAULT 0, " +
                    "thread INT NOT NULL, " +
                    "`date` VARCHAR(19) NOT NULL, " +
                    "`user` VARCHAR(255) NOT NULL, " +
                    "forum VARCHAR(255) NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "isApproved TINYINT NOT NULL DEFAULT 0, " +
                    "isHighlighted TINYINT NOT NULL DEFAULT 0, " +
                    "isEdited TINYINT NOT NULL DEFAULT 0, " +
                    "isSpam TINYINT NOT NULL DEFAULT 0, " +
                    "isDeleted TINYINT NOT NULL DEFAULT 0, " +
                    "likes INT NOT NULL DEFAULT 0, " +
                    "dislikes INT NOT NULL DEFAULT 0, " +
                    "points INT NOT NULL DEFAULT 0, " +
                    "treePath VARCHAR(255) NULL DEFAULT 0," +
                    "PRIMARY KEY (id));");
            System.out.println("Post succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createThread() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + DBConnectionManager.DBNAME + ".Thread;");
            statement.execute("CREATE TABLE IF NOT EXISTS " + DBConnectionManager.DBNAME + ".Thread (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "slug VARCHAR(255) NOT NULL, " +
                    "`date` VARCHAR(19) NOT NULL, " +
                    "`user` VARCHAR(255) NOT NULL, " +
                    "forum VARCHAR(255) NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "isDeleted TINYINT NOT NULL DEFAULT 0, " +
                    "isClosed TINYINT NOT NULL DEFAULT 0, " +
                    "likes INT NOT NULL DEFAULT 0, " +
                    "dislikes INT NOT NULL DEFAULT 0, " +
                    "points INT NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY (id));");
            System.out.println("Thread succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createSubscriptionMap() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS " + DBConnectionManager.DBNAME + ".SubscriptionMap;");
            statement.execute("CREATE TABLE IF NOT EXISTS " + DBConnectionManager.DBNAME + ".SubscriptionMap (" +
                    "`user` VARCHAR(255) NOT NULL, " +
                    "thread INT NOT NULL);");
            System.out.println("SubscriptionMap succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }

    private static void createTriggers() {
        Connection connection = DBConnectionManager.getInstance().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DROP TRIGGER IF EXISTS `update_thread_posts_counter`");
            statement.execute("CREATE TRIGGER `update_thread_posts_counter` AFTER INSERT ON " + DBConnectionManager.DBNAME + ".Post " +
                    "FOR EACH ROW " +
                    "UPDATE " + DBConnectionManager.DBNAME + ".Thread " +
                    "SET posts = posts + 1 " +
                    "WHERE " + DBConnectionManager.DBNAME + ".Thread.id = NEW.thread");
            System.out.println("Triggers succesfully created!");
        } catch (SQLException ex) {
            DBConnectionManager.printSQLExceptionData(ex);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                DBConnectionManager.printSQLExceptionData(ex);
            }
        }
    }
}
