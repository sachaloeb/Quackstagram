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
import java.util.stream.Collectors;

import src.DataStorage.User;
import src.observers.Observer;

public class ImageLikesManager implements Observer{

    private static final String likesFilePath = "data/likes.txt";

    private static final Path detailsPath = Paths.get("img", "image_details.txt");

    private String trackingImageId;


    public ImageLikesManager(String trackingImageId){
        this.trackingImageId = trackingImageId;
    }

    // Method to like an image
    public void update(){
        boolean hasAlreadyLiked = checkLike(trackingImageId, User.currentUser);
        Map<String, Set<String>> likesMap = readLikes();
        if(!likesMap.containsKey(trackingImageId)){ likesMap.put(trackingImageId, new HashSet<>()); }

        updateLikes(trackingImageId, hasAlreadyLiked);
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
        try (BufferedReader reader = new BufferedReader(new FileReader(likesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String imageID = parts[0];
                Set<String> users = Arrays.stream(parts[1].split(",")).collect(Collectors.toSet());
                likesMap.put(imageID, users);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return likesMap;
    }

    // Method to save likes to file
    private static void manageLike(Map<String, Set<String>> likesMap, String username, String imageID, boolean hasAlreadyLiked){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(likesFilePath, false));
            for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {

                String currentImageID = entry.getKey();
                String line = currentImageID + ":" + String.join(",", entry.getValue());

                if(currentImageID.equals(imageID)){



                    if(!hasAlreadyLiked) {//add like

                        if (entry.getValue().isEmpty()) {
                            line = line + username;
                        } else {
                            line = line + "," + username;
                        }
                    }
                    else{

                        line = deleteUserFromLine(line, username);

                    }

                }

                writer.write(line);

                if(!line.isEmpty()){
                    writer.newLine();
                }
            }
            writer.close();
        } catch (Exception e) {
            // TODO: handle exception
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

    private void updateLikes(String imageId,boolean hasAlreadyLiked){
        StringBuilder newContent = generateNewImageDetails(imageId, hasAlreadyLiked);
        writeNewImageDetails(newContent);
    }

    private static StringBuilder generateNewImageDetails(String imageId, boolean hasAlreadyLiked){
        StringBuilder newContent = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(detailsPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ImageID: " + imageId)) {
                    String imageLine = line;
                    line = reader.readLine();
                    while (line != null && !(line.contains("_$SEPARATOR$_"))){
                        imageLine += "\n" + line;
                        line = reader.readLine();
                    }
                    String[] parts = imageLine.split("\\_\\$separator\\$\\_");
                    int likes = Integer.parseInt(parts[4].split(": ")[1]);

                    if(hasAlreadyLiked){
                        likes--; // Increment the likes count
                    }
                    else{
                        likes++; // Increment the likes count
                    }

                    parts[4] = "Likes: " + likes;
                    imageLine = String.join("_$separator$_", parts);

                    newContent.append(imageLine);
                    if(line != null && line.contains("_$SEPARATOR$_")){ newContent.append("\n_$SEPARATOR$_\n"); }
                }
                else newContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newContent;
    }

    private static void writeNewImageDetails(StringBuilder newContent){
        try (BufferedWriter writer = Files.newBufferedWriter(detailsPath)) {
            writer.write(newContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] getLikesAsArray(String imageID){
        Map<String, Set<String>> likesMap = readLikes();
        String line = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(likesFilePath))) {
            for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {

                String currentImageID = entry.getKey();
                if(currentImageID.equals(imageID))
                {
                    line = String.join(",", entry.getValue());
                    break;
                }
            }
            return (line.split(","));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
