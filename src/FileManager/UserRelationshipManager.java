package src.FileManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import src.DataStorage.User;
import src.observers.Observable;
import src.observers.Observer;

import static src.DataStorage.SQLDBConnection.getConnection;

public class UserRelationshipManager implements Observer{
    private Observable subject;

    public UserRelationshipManager(Observable subject){
        this.subject = subject;
    }

    // Method to follow a user
    public static void followUser(String follower, String followed){

        String query = "INSERT INTO Follows (follower_username, followed_username) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unfollowUser(String follower, String followed){
        String query = "DELETE FROM Follows WHERE follower_username = ? AND followed_username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to check if a user is already following another user
    public static boolean isAlreadyFollowing(String follower, String followed){
        String query = "SELECT COUNT(*) AS FollowingBool FROM Follows WHERE follower_username = ? AND followed_username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, follower);
            stmt.setString(2, followed);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    // Method to get the list of followers for a user
    public static String getFollowers(String username) {
        StringBuilder followers = new StringBuilder();
        String query = "SELECT follower_username FROM Follows WHERE followed_username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                followers.append(rs.getString("follower_username")).append(",");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followers.toString();
    }

    public static String getFollowings(String username) {
        StringBuilder followers = new StringBuilder();
        String query = "SELECT followed_username FROM Follows WHERE follower_username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                followers.append(rs.getString("followed_username")).append(",");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followers.toString();
    }

    public static String[] getFollowersAsArray(String user){
        String followers = UserRelationshipManager.getFollowers(user);
        return followers.split(";");
    }

    @Override
    public void update() {
        String data = subject.getData();
        if(isAlreadyFollowing(User.currentUser, data)){ unfollowUser(User.currentUser, data); }
        else{ followUser(User.currentUser, data); }
    }

    public static void main(String[] args) {
        followUser("sacha", "1");
    }
}
