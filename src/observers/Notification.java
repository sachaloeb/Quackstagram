package src.observers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import src.DataStorage.ReadImageDetails;
import src.DataStorage.User;
import src.FileManager.ImageLikesManager;
import src.FileManager.UserRelationshipManager;

import static src.DataStorage.SQLDBConnection.getConnection;

public class Notification implements Observer{
    public static final String LIKE_NOTIFICATION = "like";
    public static final String FOLLOW_NOTIFICATION = "follow";

    private String trackingOperation;
    private Observable subject;



    public Notification(String trackingOperation, Observable subject){
        this.trackingOperation = trackingOperation;
        this.subject = subject;
    }

    @Override
    public void update(){
        String data = subject.getData();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String notification;

        switch(trackingOperation){
            case LIKE_NOTIFICATION:
                if(!ImageLikesManager.checkLike(data, User.currentUser)){ return; }

                ReadImageDetails details = new ReadImageDetails();
                details.loadDetails(data);

//                notification = String.format("%s; %s; %s; %s; %s\n", trackingOperation, User.currentUser, details.getUsername(), data, timestamp);
//                    try (BufferedWriter notificationWriter = Files.newBufferedWriter(Paths.get("quack/data", "notifications.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
//                        notificationWriter.write(notification);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                
                break;
            case FOLLOW_NOTIFICATION:
                    if(!UserRelationshipManager.isAlreadyFollowing(User.currentUser, data)){ return; }

//                    notification = String.format("%s; %s; %s; %s\n", trackingOperation, User.currentUser, data, timestamp);
//                    try (BufferedWriter notificationWriter = Files.newBufferedWriter(Paths.get("quack/data", "notifications.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
//                        notificationWriter.write(notification);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
        
                break;
        }
    }

//    private void saveNotification(String notifReceiver, String concernedUser, String notifType, String likedPicture, String notifTimestamp) {
//        String query = "INSERT INTO Notifications (notif_receiver, concerned_user, notif_type, liked_picture, notif_timestamp) VALUES (?, ?, ?, ?, ?)";
//
//        try (Connection conn = getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, notifReceiver);
//            stmt.setString(2, concernedUser);
//            stmt.setString(3, notifType);
//            stmt.setString(4, likedPicture);
//            stmt.setString(5, notifTimestamp);
//
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {

    }
}
