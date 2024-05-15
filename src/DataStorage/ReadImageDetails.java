package src.DataStorage;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadImageDetails {
    private String id = "";
    private String username = "";
    private String bio = "";
    private String timestampString = "";
    private int likes = 0;

    public void loadDetails(String imageID) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("img", "image_details.txt"))) {
            String line;
            while ((line = reader.readLine()) != null){
                if(line.contains(imageID)){
                    String imageLine = line;
                    line = reader.readLine();
                    while (line != null && !(line.contains("_$SEPARATOR$_"))){
                        imageLine += "\n" + line;
                        line = reader.readLine();
                    }

                    String[] parts = imageLine.split("\\_\\$separator\\$\\_");
                    username = parts[1].split(": ")[1];
                    bio = parts[2].split(": ")[1];
                    timestampString = parts[3].split(": ")[1];
                    likes = Integer.parseInt(parts[4].split(": ")[1]);
                    id = imageID;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle exception
        }
    }

    // Getters for encapsulated fields
    public String getUsername() {return username;}

    public String getBio() {return bio;}

    public String getTimestampString() {return timestampString;}

    public int getLikes() {return likes;}

    public String getID() {return id;}
}