package src.DataStorage;

import java.sql.*;

public class SQLDBConnection {
    public static void main(String[] args) {
        Class.forName(com.mysql.jdbc.Driver);
        Connection myCon =DriverManager.getConnection("jdbc:mysql://localhost:3306/TransitorDB", "root", "");

        Statement stmt = myCon.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Agency");
    }
}
