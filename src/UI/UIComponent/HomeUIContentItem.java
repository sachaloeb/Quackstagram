package src.UI.UIComponent;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.ImageScalor;
import src.DataStorage.ReadImageDetails;
import src.FileManager.ImageLikesManager;
import src.UI.QuakstagramHomeUI;
import src.observers.Notification;
import src.observers.Observable;
import src.observers.Observer;

public class HomeUIContentItem extends JPanel implements Observable{
    private static final int WIDTH = 300;
    private static final int IMAGE_WIDTH = WIDTH - 100; // Width for the image posts
    private static final int IMAGE_HEIGHT = 150; // Height for the image posts
    private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95); // Color for the like button

    private String imageID;
    private ArrayList<Observer> observers = new ArrayList<Observer>();


    public HomeUIContentItem(String[] postData, QuakstagramHomeUI homeUI){
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE); // Set the background color for the item panel
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setAlignmentX(CENTER_ALIGNMENT);
            JLabel nameLabel = new JLabel(postData[0]);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Crop the image to the fixed size
            
            JLabel imageLabel = new JLabel();
            imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border to image label
            imageID = new File(postData[3]).getName().split("\\.")[0];
            // Make the image clickable
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        String imagePath = "quack/img/uploaded/" + imageID + ".png";
                        homeUI.displayImage(imagePath); // Call a method to switch to the image view
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            ImageIcon imageIcon = new ImageIcon(postData[3]);
            imageIcon.setImage(ImageScalor.scaleImage(imageIcon.getImage(), IMAGE_WIDTH, IMAGE_HEIGHT));
            imageLabel.setIcon(imageIcon);

            JLabel descriptionLabel = new JLabel(postData[1]);
            descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            LikesLabel likesLabel = new LikesLabel(imageID);
            likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton likeButton = new JButton("❤");
            likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
            likeButton.setOpaque(true);
            likeButton.setBorderPainted(false); // Remove border

            add(nameLabel);
            add(imageLabel);
            add(descriptionLabel);
            add(likesLabel);
            add(likeButton);

            addObserver(new ImageLikesManager(imageID));
            addObserver(likesLabel);
            addObserver(new Notification(Notification.LIKE_NOTIFICATION, this));

            likeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        notifyObservers();
                }
            });
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
        return imageID;
    }


    private class LikesLabel extends JLabel implements Observer{
        private String trackingImageID;


        public LikesLabel(String trackingImageID){
            this.trackingImageID = trackingImageID;
            update();
        }

        @Override
        public void update() {
            ReadImageDetails details = new ReadImageDetails();
            details.loadDetails(trackingImageID);

            setText("Likes: " + details.getLikes());
        }
    }
}
