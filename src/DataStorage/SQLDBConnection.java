package src.DataStorage;

import java.sql.*;



public class SQLDBConnection {
    public static void main(String[] args) throws ClassNotFoundException{
        try{
            Connection myCon =DriverManager.getConnection("jdbc:mysql://localhost:3306/QuackDB", "root", "");

            Statement stmt = myCon.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users"); 
            while (rs.next()){
                System.out.print(rs.getString("username"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
    }
}
