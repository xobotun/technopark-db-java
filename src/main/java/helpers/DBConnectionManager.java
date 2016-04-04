package helpers;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class DBConnectionManager implements AutoCloseable {
    public static DBConnectionManager getInstance(){
        return instance;
    }

    public Connection getConnection() {
        if (connection == null)
            createConnection(false);
        return connection;
    }

    public Connection getRootConnection() {
        if (rootConnection == null)
            createConnection(true);
        return rootConnection;
    }

    private void createConnection(boolean isRoot) {
        if ( driver == null) {
            try {
                driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
                System.out.println("Driver succesfully registered!");
            } catch (Exception ex) {
                System.out.println("Could not register driver!");
                ex.printStackTrace();
                return;
            }
        }

        try {
            DriverManager.registerDriver(driver);
            if (!isRoot)
                connection = DriverManager.getConnection(ADDRESS + DBNAME + UNICODE, LOGIN, PASSWORD);
            else
                rootConnection = DriverManager.getConnection(ADDRESS + UNICODE, ROOT_LOGIN, ROOT_PASSWORD);
            System.out.println("Connection succesfully opened!");
        } catch (SQLException ex) {
            printSQLExceptionData(ex);
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            connection.close();
            System.out.println("Connection succesfully closed!");
        } catch (SQLException ex) {
            printSQLExceptionData(ex);
        }


    }

    public static void printSQLExceptionData(SQLException exception) {
        System.out.println("An error has encountered: " + exception.getErrorCode());
        exception.printStackTrace();
    }

    private static DBConnectionManager instance = new DBConnectionManager();

    private Driver driver = null;
    private Connection connection = null;
    private Connection rootConnection = null;

    public static final String ROOT_LOGIN = "root";
    public static final String ROOT_PASSWORD = "root";
    public static final String LOGIN = "technopark_db_java";
    public static final String DOMAIN = "localhost";
    public static final String LOGIN_DOMAIN = LOGIN + '@' + DOMAIN;
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "jdbc:mysql://" + DOMAIN + ":3306/";
    public static final String DBNAME = "technopark_db_java";
    public static final String UNICODE = "?useUnicode=true&characterEncoding=utf8";

}

