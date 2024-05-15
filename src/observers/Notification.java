package src.observers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import src.DataStorage.ReadImageDetails;
import src.DataStorage.User;
import src.FileManager.ImageLikesManager;
import src.FileManager.UserRelationshipManager;

public class Notification implements Observer{
    public static final String LIKE_NOTIFICATION = "LIKE";
    public static final String FOLLOW_NOTIFICATION = "FOLLOW";

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

                notification = String.format("%s; %s; %s; %s; %s\n", trackingOperation, User.currentUser, details.getUsername(), data, timestamp);
                    try (BufferedWriter notificationWriter = Files.newBufferedWriter(Paths.get("quack/data", "notifications.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                        notificationWriter.write(notification);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                
                break;
            case FOLLOW_NOTIFICATION:
                    if(!UserRelationshipManager.isAlreadyFollowing(User.currentUser, data)){ return; }

                    notification = String.format("%s; %s; %s; %s\n", trackingOperation, User.currentUser, data, timestamp);
                    try (BufferedWriter notificationWriter = Files.newBufferedWriter(Paths.get("quack/data", "notifications.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                        notificationWriter.write(notification);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        
                break;
        }
    }
}
