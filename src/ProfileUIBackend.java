package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import src.ImageScalor;
import src.DataStorage.User;
import src.FileManager.UserRelationshipManager;
import src.UI.UIComponent.NavigationBar;
import src.observers.Notification;
import src.observers.Observable;
import src.observers.Observer;

public class ProfileUIBackend {

   public static boolean EXisCurrentUser(User currentUser){
        String loggedInUsername = "";
        // Read the logged-in user's username from users.txt
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                loggedInUsername = line.split(":")[0].trim();
            return loggedInUsername.equals(currentUser.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void EXmanageFollowing(String currentUserUsername, String usernameToFollow, Path followingFilePath) throws IOException{
        if (!currentUserUsername.isEmpty()) {
            boolean found = false;
            StringBuilder newContent = new StringBuilder();

            // Read and process following.txt
            if (Files.exists(followingFilePath)) {
                try (BufferedReader reader = Files.newBufferedReader(followingFilePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts[0].trim().equals(currentUserUsername)) {
                            found = true;
                            if (!line.contains(usernameToFollow)) {
                                line = line.concat(line.endsWith(":") ? "" : "; ").concat(usernameToFollow);
                            }
                        }
                        newContent.append(line).append("\n");
                    }
                }
            }

            // If the current user was not found in following.txt, add them
            if (!found) {
                newContent.append(currentUserUsername).append(": ").append(usernameToFollow).append("\n");
            }

            // Write the updated content back to following.txt
            try (BufferedWriter writer = Files.newBufferedWriter(followingFilePath)) {
                writer.write(newContent.toString());
            }
        }
    }

    public static void EXwriteNewTempImageDetailsFile(String imageID, String tempImageDetailsFilePathString, String imageDetailsFilePathString) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(imageDetailsFilePathString));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempImageDetailsFilePathString, false))){

            String currentLine;
            String currentImageID = null;

            while ((currentLine = reader.readLine()) != null) {

                if(!currentLine.equals("_$SEPARATOR$_")){

                    int indexOfImageID = 9; //as all lines begin with "Image ID: "
                    currentImageID = currentLine.substring(indexOfImageID, currentLine.indexOf("_$separator$"));

                    if(!currentImageID.equals(imageID)){
                        writer.write("_$SEPARATOR$_");
                        writer.newLine();
                        writer.write(currentLine);
                        writer.newLine();
                    }

                }

            }
        }
    }





}