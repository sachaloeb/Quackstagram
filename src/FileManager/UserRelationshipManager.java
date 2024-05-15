package src.FileManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import src.DataStorage.User;
import src.observers.Observable;
import src.observers.Observer;

public class UserRelationshipManager implements Observer{

    private static final Path followingFilePath = Paths.get("data", "following.txt");
    private Observable subject;

    public UserRelationshipManager(Observable subject){
        this.subject = subject;
    }

    // Method to follow a user
    public static void followUser(String follower, String followed){
        boolean found = false;
        StringBuilder newContent = new StringBuilder();

        if (Files.exists(followingFilePath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(followingFilePath);
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts[0].equals(follower)) {
                        found = true;
                            line = line.concat(line.endsWith(":") ? "" : ";").concat(followed);
                    }
                    newContent.append(line).append("\n");
                }
                reader.close();
            }
            catch (Exception e){}
        }

        // If the current user was not found in following.txt, add them
        if (!found) {
            newContent.append(follower).append(":").append(followed).append("\n");
        }

        // Write the updated content back to following.txt
        try{
            BufferedWriter writer = Files.newBufferedWriter(followingFilePath);
            writer.write(newContent.toString());
            writer.close();
        } catch(Exception e){};
    }

    public static void unfollowUser(String follower, String followed){
        StringBuilder newContent = new StringBuilder();

        if (Files.exists(followingFilePath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(followingFilePath);
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts[0].equals(follower)) {
                        ArrayList<String> followers = new ArrayList<String>(Arrays.asList(parts[1].split(";")));
                        followers.remove(followed);
                        line = parts[0] + ":" + String.join(";", followers);
                    }
                    newContent.append(line).append("\n");
                }
                reader.close();
            }
            catch (Exception e){}
        }

        // Write the updated content back to following.txt
        try{
            BufferedWriter writer = Files.newBufferedWriter(followingFilePath);
            writer.write(newContent.toString());
            writer.close();
        } catch(Exception e){};
    }

    // Method to check if a user is already following another user
    public static boolean isAlreadyFollowing(String follower, String followed){
        try (BufferedReader reader = Files.newBufferedReader(followingFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(follower)) {
                    String[] followedUsers = parts[1].split(";");
                    for (String followedUser : followedUsers) {
                        if (followedUser.equals(followed)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    // Method to get the list of followers for a user
    public static String getFollowers(String userName){
        String followers = "";

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "following.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(userName + ":")) {
                    followers = line.split(":")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return followers;
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
}
