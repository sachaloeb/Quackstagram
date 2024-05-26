package src.DataStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLDBConnection {

    private static final Logger LOGGER = Logger.getLogger(SQLDBConnection.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/QuackDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed connection");
            }
            LOGGER.log(Level.INFO, "Success");
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error connecting", e);
            throw e; 
        }
    }
}
