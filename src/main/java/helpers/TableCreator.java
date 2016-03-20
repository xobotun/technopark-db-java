package helpers;

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

    private static void createDB() {
        Connection connection = DBConnectionManager.getInstance().getRootConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();

            statement.execute("DROP USER IF EXISTS " + DBConnectionManager.LOGIN_DOMAIN);
            statement.execute("CREATE USER " + DBConnectionManager.LOGIN_DOMAIN + " IDENTIFIED BY \"" + DBConnectionManager.PASSWORD + "\";");

            statement.execute("DROP DATABASE IF EXISTS " + DBConnectionManager.DBNAME + ";");
            statement.execute("CREATE DATABASE IF NOT EXISTS " + DBConnectionManager.DBNAME + ";");

            statement.execute("GRANT ALL ON " + DBConnectionManager.DBNAME + ".* TO " + DBConnectionManager.LOGIN_DOMAIN + ";");

            statement.close();
            System.out.println("Database succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating database: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
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
                    "id INT NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "shortName VARCHAR(255) NOT NULL, " +
                    "user INT NOT NULL, " +
                    "PRIMARY KEY (id));");
            statement.close();
            System.out.println("Forum succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating Forum: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
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
                              "PRIMARY KEY (id));");
            statement.close();
            System.out.println("User succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating User: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
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
                    "follower INT NOT NULL, " +
                    "followee INT NOT NULL);");
            statement.close();
            System.out.println("FollowMap succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating FollowMap: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
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
                    "parent VARCHAR(255) NULL DEFAULT 0, " +
                    "thread INT NOT NULL, " +
                    "date DATETIME NOT NULL, " +
                    "user INT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "isApproved TINYINT NOT NULL DEFAULT 0, " +
                    "isHighlighted TINYINT NOT NULL DEFAULT 0, " +
                    "isEdited TINYINT NOT NULL DEFAULT 0, " +
                    "isSpam TINYINT NOT NULL DEFAULT 0, " +
                    "isDeleted TINYINT NOT NULL DEFAULT 0, " +
                    "likes INT NOT NULL DEFAULT 0, " +
                    "dislikes INT NOT NULL DEFAULT 0, " +
                    "points INT NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY (id));");
            statement.close();
            System.out.println("Post succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating Post: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
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
                    "date DATETIME NOT NULL, " +
                    "user INT NOT NULL, " +
                    "forum INT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "isDeleted TINYINT NOT NULL DEFAULT 0, " +
                    "isClosed TINYINT NOT NULL DEFAULT 0, " +
                    "likes INT NOT NULL DEFAULT 0, " +
                    "dislikes INT NOT NULL DEFAULT 0, " +
                    "points INT NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY (id));");
            statement.close();
            System.out.println("Thread succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating Thread: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
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
                    "user INT NOT NULL, " +
                    "thread INT NOT NULL);");
            statement.close();
            System.out.println("SubscriptionMap succesfully created!");
        } catch (SQLException ex) {
            System.out.println("Error during creating SubscriptionMap: " + ex.getErrorCode());
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ex) {
                System.out.println("Error during closing statement: " + ex.getErrorCode());
                ex.printStackTrace();
            }
        }
    }
}
