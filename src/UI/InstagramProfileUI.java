package src.UI;
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
import src.ProfileUIBackend;
import src.DataStorage.User;
import src.FileManager.ImageHashTagsManager;
import src.FileManager.UserRelationshipManager;
import src.UI.UIComponent.NavigationBar;
import src.observers.Notification;
import src.observers.Observable;
import src.observers.Observer;


public class InstagramProfileUI implements Observable{

    private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = uiWindow.getWidth() / 3; // Static size for grid images
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel;   // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information
    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private final NavigationBar navigationBar = new NavigationBar();

    private final Path imageDetailsFilePath = Paths.get("quack/img", "image_details.txt");

    private final Path tempImageDetailsFilePath = Paths.get("quack/img", "image_details_temp.txt");

    private final String imageDetailsFilePathString = "quack/img/image_details.txt";

    private final String tempImageDetailsFilePathString = "quack/img/image_details_temp.txt";

    public InstagramProfileUI(User user) {
        uiWindow.clearWindow();
        uiWindow.setVisible(true);
        
        this.currentUser = user;
         // Initialize counts
        int imageCount = 0;
        int followersCount = 0;
        int followingCount = 0;
       
            // Step 1: Read image_details.txt to count the number of images posted by the user
        try (BufferedReader imageDetailsReader = Files.newBufferedReader(imageDetailsFilePath)) {
            String line;
            while ((line = imageDetailsReader.readLine()) != null) {
                if (line.contains("Username: " + currentUser.getUsername())) {
                    imageCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 2: Read following.txt to calculate followers and following
        Path followingFilePath = Paths.get("quack/data", "following.txt");
        try (BufferedReader followingReader = Files.newBufferedReader(followingFilePath)) {
            String line;
            while ((line = followingReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String[] followingUsers = parts[1].split(";");
                    if (username.equals(currentUser.getUsername())) {
                        followingCount = followingUsers.length;
                    } else {
                        for (String followingUser : followingUsers) {
                            if (followingUser.trim().equals(currentUser.getUsername())) {
                                followersCount++;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String bio = "";

        Path bioDetailsFilePath = Paths.get("quack/data", "userBios.txt");
        Path usernamesFilePath = Paths.get("quack/data", "credentialsUsernames.txt");

        try (BufferedReader bioDetailsReader = Files.newBufferedReader(bioDetailsFilePath);
             BufferedReader usernameReader = Files.newBufferedReader(usernamesFilePath)
        ) {
            while ((bio = bioDetailsReader.readLine()) != null) {
                String username = usernameReader.readLine();
                if (username.equals(currentUser.getUsername())) {
                    //bio is already set to the correct one
                    break; // Exit the loop once the matching bio is found
                }
            }
            bio = "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentUser.setBio(bio);


        currentUser.setFollowersCount(followersCount);
        currentUser.setFollowingCount(followingCount);
        currentUser.setPostCount(imageCount);

        uiWindow.setTitle("DACS Profile");
        initializeUI();
    }


      public InstagramProfileUI() {
        uiWindow.setVisible(true);
        uiWindow.setTitle("DACS Profile");

        initializeUI();
    }
    private void initializeUI() {

        contentPanel = new JPanel();
        headerPanel = createHeaderPanel();       // Initialize header panel
        navigationPanel = navigationBar.getNavigationBar(); // Initialize navigation panel
        
        // Re-add the header and navigation panels
        uiWindow.add(headerPanel, BorderLayout.NORTH);
        uiWindow.add(navigationPanel, BorderLayout.SOUTH);

        // Initialize the image grid
        initializeImageGrid();

        uiWindow.refresh();
    }

    private boolean isCurrentUser(){
    return ProfileUIBackend.EXisCurrentUser(currentUser);
}

    private JPanel createHeaderPanel() {
        boolean isCurrentUser = isCurrentUser();
    
       // Header Panel
        JPanel headerPanel = new JPanel();
        try (Stream<String> lines = Files.lines(Paths.get("quack/data", "users.txt"))) {
            isCurrentUser = lines.anyMatch(line -> line.startsWith(currentUser.getUsername() + ":"));
        } catch (IOException e) {
            e.printStackTrace();  // Log or handle the exception as appropriate
        }

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.GRAY);
        
        // Top Part of the Header (Profile Image, Stats, Follow Button)
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(new Color(249, 249, 249));

        // Profile image
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("quack/img/storage/profile/"+currentUser.getUsername()+".png").getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topHeaderPanel.add(profileImage, BorderLayout.WEST);

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

        // Follow Button
        JButton followButton = createFollowButton(isCurrentUser);

        // Add Stats and Follow Button to a combined Panel
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS));
        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followButton);

        topHeaderPanel.add(statsFollowPanel, BorderLayout.CENTER);
        headerPanel.add(topHeaderPanel);

     // Profile Name and Bio Panel
        JPanel profileNameAndBioPanel = createProfileNameAndBioPanel();

        headerPanel.add(profileNameAndBioPanel);

        return headerPanel;

}

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getPostsCount()) , "Posts"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowingCount()), "Following"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0)); // Add some vertical padding
        return statsPanel;
    }

    private JPanel createProfileNameAndBioPanel() {
        JPanel profileNameAndBioPanel = new JPanel();
        profileNameAndBioPanel.setLayout(new BorderLayout());
        profileNameAndBioPanel.setBackground(new Color(249, 249, 249));

        JLabel profileNameLabel = new JLabel(currentUser.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding on the sides

        JTextArea profileBio = new JTextArea(currentUser.getBio());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(new Color(249, 249, 249));
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding on the sides

        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);
        return profileNameAndBioPanel;
    }

    private JButton createFollowButton(boolean isCurrentUser) {
        JButton followButton;
        if (isCurrentUser) {
            followButton = new JButton("Edit Profile");
        } else {
            followButton = new FollowButton(currentUser.getUsername());
            addObserver(new UserRelationshipManager(this));
            addObserver((FollowButton)followButton);
            addObserver(new Notification(Notification.FOLLOW_NOTIFICATION, this));

            followButton.addActionListener(e -> {
                notifyObservers();
            });
        }

        formatFollowButton(followButton);
        return followButton;
    }

    private static void formatFollowButton(JButton followButton) {
        followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followButton.setFont(new Font("Arial", Font.BOLD, 12));
        followButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height)); // Make the button fill the horizontal space
        followButton.setBackground(new Color(225, 228, 232)); // A soft, appealing color that complements the UI
        followButton.setForeground(Color.BLACK);
        followButton.setOpaque(true);
        followButton.setBorderPainted(false);
        followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some vertical padding
    }

public void manageFollowing(String currentUserUsername, String usernameToFollow, Path followingFilePath) throws IOException {
  
   ProfileUIBackend.EXmanageFollowing( currentUserUsername,usernameToFollow,followingFilePath);
}

private void initializeImageGrid() {
    contentPanel.removeAll(); // Clear existing content
    contentPanel.setLayout(new GridLayout(0, 3, 5, 5)); // Grid layout for image grid

    Path imageDir = Paths.get("quack/img", "uploaded");
    try (Stream<Path> paths = Files.list(imageDir)) {
        paths.filter(path -> path.getFileName().toString().startsWith(currentUser.getUsername() + "_"))
             .forEach(path -> {

                 ImageIcon imageIcon = new ImageIcon(path.toString());
                 ImageIcon imageMini = new ImageIcon(new ImageIcon(path.toString()).getImage().getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
                 JLabel imageLabel = new JLabel(imageMini);

                 imageLabel.addMouseListener(new MouseAdapter() {
                     @Override
                     public void mouseClicked(MouseEvent e) {
                         displayImage(imageIcon, path); // Call method to display the clicked image
                     }
                 });
                 contentPanel.add(imageLabel);
             });
    } catch (IOException ex) {
        ex.printStackTrace();
        // Handle exception (e.g., show a message or log)
    }

    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    uiWindow.add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center
    uiWindow.refresh();
}

private String getImageIdFromPath(Path imagePath){
    String fileName = imagePath.toString().split("\\\\")[0];
    int dotIndex = fileName.lastIndexOf('.');
    return fileName.substring(0, dotIndex);
}


    private void displayImage(ImageIcon imageIcon, Path imagePath) {
        contentPanel.removeAll(); // Remove existing content
        contentPanel.setLayout(new BorderLayout()); // Change layout for image display

        JPanel backButtonPanel = createBackButtonPanel();

        contentPanel.add(backButtonPanel, BorderLayout.NORTH);

        ImageIcon fullSizeImageIcon = new ImageIcon(ImageScalor.scaleImage(imageIcon.getImage(), (uiWindow.getWidth() - 20), (uiWindow.getHeight() - headerPanel.getHeight() - navigationPanel.getHeight() - 135)));
        JLabel fullSizeImageLabel = new JLabel(fullSizeImageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        createPersonalButtons(imagePath, buttonPanel);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        uiWindow.refresh();
    }

    private JPanel createBackButtonPanel(){
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    
    
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(uiWindow.getWidth()-20, backButton.getPreferredSize().height));
    
        backButton.addActionListener(e -> {
            uiWindow.clearWindow(); // Remove all components from the frame
            initializeUI(); // Re-initialize the UI
        });

        backButtonPanel.add(backButton);
        return backButtonPanel;
    }

    private void createPersonalButtons(Path imagePath, JPanel buttonPanel) {
        JButton addHashTagButton = null;
        JButton deleteImageButton = null;
        String imageID = getImageIdFromPath(imagePath);
        if(isCurrentUser()){
            deleteImageButton = new JButton("Delete Image");

            deleteImageButton.addActionListener(e -> {


                try {
                    deleteImage(imagePath);

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                uiWindow.clearWindow(); // Remove all components from the frame
                initializeUI(); // Re-initialize the UI
            });

            addHashTagButton = new JButton("Add hashtag");

            addHashTagButton.addActionListener(e -> {

                String hashtag = JOptionPane.showInputDialog(null,
                        "Enter your hashtag", null);
                if(!(hashtag == null) && !hashtag.isEmpty()){ ImageHashTagsManager.addHashTag(imageID, hashtag);}
            });

            buttonPanel.add(deleteImageButton);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(addHashTagButton);

        }
    }

    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    private void deleteImage(Path imagePath) throws IOException {
        String imageID = getImageIdFromPath(imagePath);

        writeNewTempImageDetailsFile(imageID);
        writeTempFileToOG();

        deleteFileFromStorage(imagePath);


    }

    private void writeNewTempImageDetailsFile(String imageID) throws IOException {

        ProfileUIBackend.EXwriteNewTempImageDetailsFile(imageID,imageDetailsFilePathString,tempImageDetailsFilePathString);
    }



    private void writeTempFileToOG() throws IOException {
        try(BufferedReader tempReader = new BufferedReader(new FileReader(tempImageDetailsFilePathString));
        BufferedWriter ogWriter = new BufferedWriter(new FileWriter(imageDetailsFilePathString))){

            String currentLine;

            while((currentLine = tempReader.readLine()) != null){
                ogWriter.write(currentLine);
                ogWriter.newLine();
            }

        }
    }

    private void deleteFileFromStorage(Path imagePath) {

        File fileToDelete = new File(String.valueOf(imagePath));

        boolean deletedFile = fileToDelete.delete();


        if(deletedFile) {
            JOptionPane.showMessageDialog(null, "Successfully deleted image!", "Image Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }


    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }


    @Override
    public void notifyObservers() {
        for(Observer o: observers){ o.update(); }
    }


    @Override
    public String getData() {
        return currentUser.getUsername();
    }


    private class FollowButton extends JButton implements Observer{
        private String trackingUser;


        public FollowButton(String trackingUser){
            this.trackingUser = trackingUser;
            update();
        }

        @Override
        public void update() {
            if(UserRelationshipManager.isAlreadyFollowing(User.currentUser, trackingUser)){setText("Following");}
            else{ setText("Follow"); }
        }
    }
    }
