package src.UI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import src.DataStorage.User;
import src.UI.UIComponent.DefaultHeader;
import src.UI.UIComponent.NavigationBar;
import src.observers.Notification;

public class NotificationsUI{

    private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();


    private final NavigationBar navigationBar = new NavigationBar();
    private final DefaultHeader header = new DefaultHeader(" Notifications 🐥");




    public NotificationsUI() {
        uiWindow.clearWindow();
        uiWindow.setTitle("Notifications");
        initializeUI();
    }

    private void initializeUI() {
        // Reuse the header and navigation panel creation methods from the InstagramProfileUI class
        JPanel headerPanel = header.getHeader();
        JPanel navigationPanel = navigationBar.getNavigationBar();

        // Content Panel for notifications
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


   try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        String[] parts = line.split(";");
        if (parts[2].trim().equals(User.currentUser)) {
            // Format the notification message
            String notificationMessage;
            String timestamp;
            switch(parts[0].trim()){
                case Notification.LIKE_NOTIFICATION:
                    String userWhoLiked = parts[2].trim();
                    String imageId = parts[3].trim();
                    timestamp = parts[4].trim();

                    notificationMessage = userWhoLiked + " liked your picture " + imageId + " " + getElapsedTime(timestamp) + " ago";
                    break;
                case Notification.FOLLOW_NOTIFICATION:
                    String userWhoFollowed = parts[2].trim();
                    timestamp = parts[3].trim();

                    notificationMessage = userWhoFollowed + " followed you " + getElapsedTime(timestamp) + " ago";
                    break;
                default: notificationMessage = "";
            }

            // Add the notification to the panel
            JPanel notificationPanel = new JPanel(new BorderLayout());
            notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
            notificationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            notificationPanel.setBackground(new Color(230, 230, 230));
            
            JLabel notificationLabel = new JLabel(notificationMessage);
            notificationPanel.add(notificationLabel, BorderLayout.CENTER);
            
            // Add profile icon (if available) and timestamp
            // ... (Additional UI components if needed)

            contentPanel.add(notificationPanel);
        }
    }
} catch (IOException e) {
    e.printStackTrace();
}
        // Add panels to frame
        uiWindow.add(headerPanel, BorderLayout.NORTH);
        uiWindow.add(scrollPane, BorderLayout.CENTER);
        uiWindow.add(navigationPanel, BorderLayout.SOUTH);

        uiWindow.refresh();
    }

private String getElapsedTime(String timestamp) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime timeOfNotification = LocalDateTime.parse(timestamp, formatter);
    LocalDateTime currentTime = LocalDateTime.now();

    long daysBetween = ChronoUnit.DAYS.between(timeOfNotification, currentTime);
    long minutesBetween = ChronoUnit.MINUTES.between(timeOfNotification, currentTime) % 60;

    StringBuilder timeElapsed = new StringBuilder();
    if (daysBetween > 0) {
        timeElapsed.append(daysBetween).append(" day").append(daysBetween > 1 ? "s" : "");
    }
    if (minutesBetween > 0) {
        if (daysBetween > 0) {
            timeElapsed.append(" and ");
        }
        timeElapsed.append(minutesBetween).append(" minute").append(minutesBetween > 1 ? "s" : "");
    }
    return timeElapsed.toString();
}
}
