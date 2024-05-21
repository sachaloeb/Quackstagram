package src.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.ImageScalor;
import src.DataStorage.ReadImageDetails;
import src.DataStorage.User;
import src.FileManager.ImageHashTagsManager;
import src.FileManager.ImageLikesManager;
import src.FileManager.UserRelationshipManager;
import src.UI.UIComponent.NavigationBar;
import src.observers.Notification;
import src.observers.Observable;
import src.observers.Observer;

public class ImageViewerUI implements Observable{

        private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();

        private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95); // Color for the like button

        private final NavigationBar navigationBar = new NavigationBar();

        private ArrayList<Observer> observers = new ArrayList<Observer>();
        private String imageId;

        private Object lastUiClass;

    public ImageViewerUI(String imagePath, Object lastUiClass) {
        uiWindow.setTitle("Image");
        this.lastUiClass = lastUiClass;
        initializeUI(imagePath);
    }

    private void initializeUI(String imagePath){
    uiWindow.setLayout(new BorderLayout());
    JPanel imageViewerPanel = createImageViewerPanel(imagePath);
    uiWindow.add(imageViewerPanel);
    uiWindow.refresh();
}

private JPanel createImageViewerPanel(String imagePath){
    JPanel imageViewerPanel = new JPanel(new BorderLayout());

    // Extract image ID from the imagePath
    imageId = new File(imagePath).getName().split("\\.")[0];
    // Read image details
    ReadImageDetails imageDetails = new ReadImageDetails();
    imageDetails.loadDetails(imageId);
    JPanel topPanel = createTopPanel(imageDetails.getUsername(), imageDetails.getTimestampString());
    // Bottom panel for bio and likes
    JPanel bottomPanel = createBottomPanel(imageDetails.getBio(), imageDetails.getLikes());
    // Prepare the image for display
    JLabel imageLabel = createImageLabel(imagePath, uiWindow.getHeight() - topPanel.getHeight() - bottomPanel.getHeight());
    // Panel for the back button
    JPanel backButtonPanel = createBackButtonPanel();
    // Container panel for image and details
    JPanel containerPanel = new JPanel(new BorderLayout());

    containerPanel.add(topPanel, BorderLayout.NORTH);
    containerPanel.add(imageLabel, BorderLayout.CENTER);
    containerPanel.add(bottomPanel, BorderLayout.SOUTH);

 imageViewerPanel.add(navigationBar.getNavigationBar(), BorderLayout.SOUTH);
 imageViewerPanel.add(backButtonPanel, BorderLayout.NORTH);
 imageViewerPanel.add(containerPanel, BorderLayout.CENTER);

    return imageViewerPanel;
}

private JPanel createTopPanel(String username, String timestampString){
    JPanel topPanel = new JPanel(new BorderLayout());

    JLabel timeLabel = new JLabel(calculateTimeSincePosting(timestampString));
    timeLabel.setHorizontalAlignment(JLabel.RIGHT);


    JButton usernameLabel = new JButton(username);

    final String finalUsername = username;
    usernameLabel.addActionListener(e -> {
        User user = new User(finalUsername); // Assuming User class has a constructor that takes a username
        uiWindow.clearWindow();
        new InstagramProfileUI(user);
    });

    topPanel.add(usernameLabel, BorderLayout.WEST);
    topPanel.add(timeLabel, BorderLayout.EAST);

    return topPanel;
}

    private String calculateTimeSincePosting(String timestampString){
        String timeSincePosting = "Unknown";

        if (!timestampString.isEmpty()) {
            LocalDateTime timestamp = LocalDateTime.parse(timestampString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime now = LocalDateTime.now();
            long days = ChronoUnit.DAYS.between(timestamp, now);
            timeSincePosting = days + " day" + (days != 1 ? "s" : "") + " ago";
        }

        return timeSincePosting;
}

    private JPanel createBottomPanel(String bio, int likes){
    JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JLabel likesAndHashTagsLabel = new LikesAndHashTagsLabel(imageId);
        likesAndHashTagsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel followedLikes = new JLabel(followedLikesStringBuilder(imageId));

        addObserver(new ImageLikesManager(imageId));
        addObserver(new Notification("like", this));
        addObserver((LikesAndHashTagsLabel)likesAndHashTagsLabel);

        JButton likeButton = new JButton("❤");
            likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
            likeButton.setOpaque(true);
            likeButton.setBorderPainted(false); // Remove border
            likeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        notifyObservers();
                }
            });

        bottomPanel.add(new JLabel(bio)); // Description
        bottomPanel.add(likesAndHashTagsLabel); // Likes
        bottomPanel.add(likeButton);
        bottomPanel.add(followedLikes);

        return bottomPanel;
}

    private JPanel createBackButtonPanel(){
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));


    JButton backButton = new JButton("Back");
    backButton.setPreferredSize(new Dimension(uiWindow.getWidth()-20, backButton.getPreferredSize().height));

    backButton.addActionListener(e -> {
        uiWindow.clearWindow();
        if(lastUiClass.getClass().equals(ExploreUI.class)){
            new ExploreUI();
        }
        else if(lastUiClass.getClass().equals(QuakstagramHomeUI.class)){
            new QuakstagramHomeUI();
        }

    });


    backButtonPanel.add(backButton);
    return backButtonPanel;
    }

    private JLabel createImageLabel(String imagePath, int imageHeight){
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            ImageIcon imageIcon = new ImageIcon(ImageScalor.scaleImage(originalImage, uiWindow.getWidth(), imageHeight));
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
        }

        return imageLabel;
    }

    private String followedLikesStringBuilder(String imageId){
        String[] followedLikesArr = ImageLikesManager.getFollowedLikes(imageId, UserRelationshipManager.getFollowersAsArray(User.currentUser));
        if(followedLikesArr.length == 0 || followedLikesArr[0].equals("")){ //if there are no followed likes return empty string
            return "";
        }

        String followedLikesText = "Liked by ";

        int followsToDisplay = 3; //How many likes from followed people will be displayed as a maximum

        for (int i = 1; i <= followedLikesArr.length; i++){
            followedLikesText = followedLikesText + followedLikesArr[i - 1];

            if(i == followedLikesArr.length){
                followedLikesText = followedLikesText + ".";
                break;
            }
            if(i == followsToDisplay){
                int otherFollows = followedLikesArr.length - followsToDisplay;
                followedLikesText = followedLikesText + " and " + otherFollows + " more.";
                break;
            }


            if(i == followedLikesArr.length - 1){
                followedLikesText = followedLikesText + " and ";
            }
            else{
                followedLikesText = followedLikesText + ", ";
            }
        }

        return followedLikesText;
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
    public String getData(){
        return imageId;
    }


    private class LikesAndHashTagsLabel extends JLabel implements Observer{
        private String trackingImageID;


        public LikesAndHashTagsLabel(String trackingImageID){
            this.trackingImageID = trackingImageID;
            update();
        }

        @Override
        public void update() {
            ReadImageDetails details = new ReadImageDetails();
            details.loadDetails(trackingImageID);

            setText("Likes: " + details.getLikes() + " " + ImageHashTagsManager.getHashTagsString(imageId));
        }
    }
}
