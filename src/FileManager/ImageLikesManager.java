package src.FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import src.DataStorage.User;
import src.observers.Observer;

import static src.DataStorage.SQLDBConnection.getConnection;

public class ImageLikesManager implements Observer{

    private String trackingImageId;


    public ImageLikesManager(String trackingImageId){
        this.trackingImageId = trackingImageId;
    }

    // Method to like an image
    public void update(){
        boolean hasAlreadyLiked = checkLike(trackingImageId, User.currentUser);
        Map<String, Set<String>> likesMap = readLikes();
        if(!likesMap.containsKey(trackingImageId)){ likesMap.put(trackingImageId, new HashSet<>()); }

//        updateLikes(trackingImageId, hasAlreadyLiked);
        manageLike(likesMap, User.currentUser, trackingImageId, hasAlreadyLiked);
    }

    public static boolean checkLike(String imageID, String user){
        Map<String, Set<String>> likesMap = readLikes();
        if (!likesMap.containsKey(imageID)) {
            likesMap.put(imageID, new HashSet<>());
        }

        Set<String> users = likesMap.get(imageID);

        if (!users.contains(user)) { return false; }
        else{ return true; }
    }

    // Method to read likes from file
    private static Map<String, Set<String>> readLikes(){
        Map<String, Set<String>> likesMap = new HashMap<>();
        String query = "SELECT * FROM Likes";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String imageID = rs.getString("imageID");
                String username = rs.getString("username");

                likesMap.computeIfAbsent(imageID, k -> new HashSet<>()).add(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return likesMap;
//        Map<String, Set<String>> likesMap = new HashMap<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(likesFilePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(":");
//                String imageID = parts[0];
//                Set<String> users = Arrays.stream(parts[1].split(",")).collect(Collectors.toSet());
//                likesMap.put(imageID, users);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return likesMap;
    }

    // Method to save likes to file
    private static void manageLike(Map<String, Set<String>> likesMap, String username, String imageID, boolean hasAlreadyLiked){
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            if (hasAlreadyLiked) {
                likesMap.get(imageID).remove(username);
                deleteLike(username, imageID);
//                String deleteQuery = "DELETE FROM Likes WHERE imageID = ? AND username = ?";
//                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
//                    deleteStmt.setString(1, imageID);
//                    deleteStmt.setString(2, username);
//                    deleteStmt.executeUpdate();
//                }
//                Set<String> users = likesMap.get(imageID);
//                if (users != null) {
//                    users.remove(username);
//                }
            } else {
                likesMap.get(imageID).add(username);
                insertLike(username, imageID);
//                String insertQuery = "INSERT INTO Likes VALUES (?, ?)";
//                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
//                    insertStmt.setString(1, imageID);
//                    insertStmt.setString(2, username);
//                    insertStmt.executeUpdate();
//                }
//                likesMap.computeIfAbsent(imageID, k -> new HashSet<>()).add(username);
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(likesFilePath, false));
//            for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {
//
//                String currentImageID = entry.getKey();
//                String line = currentImageID + ":" + String.join(",", entry.getValue());
//
//                if(currentImageID.equals(imageID)){
//
//
//
//                    if(!hasAlreadyLiked) {//add like
//
//                        if (entry.getValue().isEmpty()) {
//                            line = line + username;
//                        } else {
//                            line = line + "," + username;
//                        }
//                    }
//                    else{
//
//                        line = deleteUserFromLine(line, username);
//
//                    }
//
//                }
//
//                writer.write(line);
//
//                if(!line.isEmpty()){
//                    writer.newLine();
//                }
//            }
//            writer.close();
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
    }

    private static void insertLike(String user, String imageId) {
        String insertQuery = "INSERT INTO likes VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setString(1, imageId);
            insertStmt.setString(2, user);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteLike(String user, String imageId) {
        String deleteQuery = "DELETE FROM likes WHERE username = ? AND imageID = ?";
        try (Connection conn = getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setString(1, user);
            deleteStmt.setString(2, imageId);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static String deleteUserFromLine(String line, String username){

        String[] separatedLine = (line.split(":"));

        String imageId = separatedLine[0];
        String usersLine = separatedLine[1];

        List<String> usernames = new ArrayList<>(Arrays.asList(usersLine.split(","))); //separate usernames into an array
        usernames.remove(username);

        String newLine = imageId + ":";

        if(usernames.isEmpty()){
            newLine = "";
        }
        else {
            for (int i = 0; i < usernames.size(); i++) {
                if (i != 0) {
                    newLine = newLine + ",";
                }
                newLine = newLine + usernames.get(i);
            }
        }
        
        return newLine;
    }

    private static void updateLikes(String imageId, boolean hasAlreadyLiked){
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // Get current likes count
            String selectQuery = "SELECT likes FROM Images WHERE imageID = ?";
            int likes = 0;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, imageId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    likes = rs.getInt("likes");
                }
            }

            if (hasAlreadyLiked) {
                likes--;
            } else {
                likes++;
            }

            String updateQuery = "UPDATE Images SET likes = ? WHERE imageID = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, likes);
                updateStmt.setString(2, imageId);
                updateStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String[] getLikesAsArray(String imageID){
        Map<String, Set<String>> likesMap = readLikes();
        String line = "";
        for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {
            String currentImageID = entry.getKey();
            if(currentImageID.equals(imageID))
            {
                line = String.join(",", entry.getValue());
                break;
            }
        }
        return (line.split(","));
    }


    public static int getLikeCount(String imageID) throws IOException {

        String[] separatedLikes = getLikesAsArray(imageID);

        if(separatedLikes[0].isEmpty()){
            return 0;
        }

        return separatedLikes.length;

    }

    public static void deleteLikes(String imageID){
        //for when an image is deleted
    }

    public static String[] getFollowedLikes(String imageID, String[] followedUsers){

        String[] separatedLikes = getLikesAsArray(imageID);
        ArrayList<String> followedLikesArrList = new ArrayList<>();

        for(int i = 0; i < separatedLikes.length; i++){
            for(int j = 0; j < followedUsers.length; j++){

                if (separatedLikes[i].equals(followedUsers[j])){
                    followedLikesArrList.add(followedUsers[j]);
                }

            }
        }

        String[] followedLikesArr = new String[followedLikesArrList.size()];
        for(int i = 0; i < followedLikesArrList.size(); i++){
            followedLikesArr[i] = followedLikesArrList.get(i);
        }
        return followedLikesArr;


    }



}
