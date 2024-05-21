package src.FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static src.DataStorage.SQLDBConnection.getConnection;


public class ImageHashTagsManager {


    public static String[] getHashTagsAsArray(String imageID) {
        Map<String, Set<String>> likesMap = readHashTags();
        String line = "";
        for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {

            String currentImageID = entry.getKey();
            if (currentImageID.equals(imageID)) {
                line = String.join(",", entry.getValue());
                break;
            }
        }
        return (line.split(","));
    }

    public static String getHashTagsString(String imageID){
        String[] separatedHashTags = getHashTagsAsArray(imageID);

        String resultLine = String.join("#",separatedHashTags);
        if(!resultLine.equals("")) resultLine = "#" + resultLine;
        
        return resultLine;
    }

    private static Map<String, Set<String>> readHashTags(){
        Map<String, Set<String>> likesMap = new HashMap<>();
        String query = "SELECT * FROM hashtags";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String imageID = rs.getString("imageID");
                String keyword = rs.getString("keyword");

                likesMap.computeIfAbsent(imageID, k -> new HashSet<>()).add(keyword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        try (BufferedReader reader = new BufferedReader(new FileReader(hashTagsFilePath))) {
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
        return likesMap;
    }

    public static void addHashTag(String imageId, String hashTag){
        Map<String, Set<String>> hashTagsMap = readHashTags();
        if(!hashTagsMap.containsKey(imageId)){ hashTagsMap.put(imageId, new HashSet<>()); }

        manageHashTags(hashTagsMap, hashTag, imageId);
    }

    private static void manageHashTags(Map<String, Set<String>> hashTagsMap, String hashTag, String imageID){
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO hashtags VALUES (?, ?)");
            PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM hashtags WHERE imageID = ?");
            deleteStatement.setString(1, imageID);
            deleteStatement.executeUpdate();
            hashTagsMap.get(imageID).add(hashTag);
            for (String tag : hashTagsMap.get(imageID)) {
                insertStatement.setString(1, tag);
                insertStatement.setString(2, imageID);
                insertStatement.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        addHashTag("Zara_1", "Amazing");
    }
}
