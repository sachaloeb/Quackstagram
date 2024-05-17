package src.DataStorage;

import java.sql.*;



public class SQLDBConnection {
    public static void main(String[] args){
        try{
            Connection myCon =DriverManager.getConnection("jdbc:mysql://localhost:3306/QuackDB", "root", "");

            Statement stmt = myCon.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Images"); 
            while (rs.next()){
                System.out.println(rs.getString("username"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
    }
}
