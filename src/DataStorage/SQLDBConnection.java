package src.DataStorage;

import java.sql.*;



public class SQLDBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/QuackDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
