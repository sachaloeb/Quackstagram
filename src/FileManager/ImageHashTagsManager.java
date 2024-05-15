package src.FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class ImageHashTagsManager {
    private static final String hashTagsFilePath = "data/hashTags.txt";


    public static String[] getHashTagsAsArray(String imageID){
        Map<String, Set<String>> likesMap = readHashTags();
        String line = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(hashTagsFilePath))) {
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

    public static String getHashTagsString(String imageID){
        String[] separatedHashTags = getHashTagsAsArray(imageID);

        String resultLine = String.join("#", separatedHashTags);
        if(!resultLine.equals("")) resultLine = "#" + resultLine;
        
        return resultLine;
    }

    private static Map<String, Set<String>> readHashTags(){
        Map<String, Set<String>> likesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(hashTagsFilePath))) {
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

    public static void addHashTag(String imageId, String hashTag){
        Map<String, Set<String>> hashTagsMap = readHashTags();
        if(!hashTagsMap.containsKey(imageId)){ hashTagsMap.put(imageId, new HashSet<>()); }

        manageHashTags(hashTagsMap, hashTag, imageId);
    }

    private static void manageHashTags(Map<String, Set<String>> hashTagsMap, String hashTag, String imageID){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(hashTagsFilePath, false));
            for (Map.Entry<String, Set<String>> entry : hashTagsMap.entrySet()) {

                String currentImageID = entry.getKey();
                String line = currentImageID + ":" + String.join(",", entry.getValue());

                if(currentImageID.equals(imageID)){

                        if (entry.getValue().isEmpty()) {
                            line = line + hashTag;
                        } else {
                            line = line + "," + hashTag;
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
}
