package src.UI.UIComponent;
import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import src.DataStorage.User;
import src.UI.*;

public class NavigationBar extends JFrame {

    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons

    private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();

    public NavigationBar (){

    }



    private JPanel createNavPanel(){
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(249, 249, 249));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navPanel.add(createIconButton("img/icons/home.png", "home"));
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(createIconButton("img/icons/search.png","explore"));
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(createIconButton("img/icons/add.png","add"));
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(createIconButton("img/icons/heart.png","notification"));
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(createIconButton("img/icons/profile.png", "profile"));

        return navPanel;
    }

    private JButton createIconButton(String iconPath, String buttonType) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);

        // Define actions based on button type
        if ("home".equals(buttonType)) {
            button.addActionListener(e -> openHomeUI());
        } else if ("profile".equals(buttonType)) {
            button.addActionListener(e -> openProfileUI());
        } else if ("notification".equals(buttonType)) {
            button.addActionListener(e -> notificationsUI());
        } else if ("explore".equals(buttonType)) {
            button.addActionListener(e -> exploreUI());
        } else if ("add".equals(buttonType)) {
            button.addActionListener(e -> ImageUploadUI());
        }
        return button;

    }

    private void openProfileUI() {
        // Open InstagramProfileUI frame
        uiWindow.clearWindow();
        User user = new User(getLoggedInUserName());
        InstagramProfileUI profileUI = new InstagramProfileUI(user);
    }

    private String getLoggedInUserName(){

        // Read the logged-in user's username from users.txt
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                return line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void notificationsUI() {
        uiWindow.clearWindow();
        NotificationsUI notificationsUI = new NotificationsUI();
    }

    private void ImageUploadUI() {
        uiWindow.clearWindow();
        ImageUploadUI upload = new ImageUploadUI();
    }

    private void openHomeUI() {
        uiWindow.clearWindow();
        QuakstagramHomeUI homeUI = new QuakstagramHomeUI();
    }

    private void exploreUI() {
        uiWindow.clearWindow();
        ExploreUI explore = new ExploreUI();
    }

    public JPanel getNavigationBar(){
        return createNavPanel();
    }


}
