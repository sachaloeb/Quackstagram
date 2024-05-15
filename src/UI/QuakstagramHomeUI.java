package src.UI;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import src.DataStorage.ReadImageDetails;
import src.DataStorage.User;
import src.FileManager.UserRelationshipManager;
import src.UI.UIComponent.DefaultHeader;
import src.UI.UIComponent.HomeUIContentItem;
import src.UI.UIComponent.NavigationBar;

public class QuakstagramHomeUI{
    private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel homePanel;
    private JPanel imageViewPanel;

    private final NavigationBar navigationBar = new NavigationBar();
    private final DefaultHeader header = new DefaultHeader("🐥 Quackstagram 🐥");



    public QuakstagramHomeUI() {
        uiWindow.setTitle("Quakstagram Home");
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        homePanel = new JPanel(new BorderLayout());
        imageViewPanel = new JPanel(new BorderLayout());

        initializeUI();

        cardPanel.add(homePanel, "Home");
        cardPanel.add(imageViewPanel, "ImageView");

        uiWindow.add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "Home"); // Start with the home view

        // Header with the Register label
        JPanel headerPanel = header.getHeader();
        uiWindow.add(headerPanel, BorderLayout.NORTH);


        // Navigation Bar
        JPanel navigationPanel = navigationBar.getNavigationBar();
        uiWindow.add(navigationPanel, BorderLayout.SOUTH);

        uiWindow.refresh();
    }

    private void initializeUI() {


        // Content Scroll Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical box layout
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Never allow horizontal scrolling
        String[][] sampleData = createSampleData();
        populateContentPanel(contentPanel, sampleData);
        uiWindow.add(scrollPane, BorderLayout.CENTER);

        // Set up the home panel

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));


        homePanel.add(scrollPane, BorderLayout.CENTER);

        uiWindow.refresh();


    }

    private void populateContentPanel(JPanel panel, String[][] sampleData) {

        for (String[] postData : sampleData) {
            JPanel itemPanel = new HomeUIContentItem(postData, this);

            panel.add(itemPanel);

            // Grey spacing panel
            JPanel spacingPanel = new JPanel();
            spacingPanel.setPreferredSize(new Dimension(uiWindow.getWidth()-10, 5)); // Set the height for spacing
            spacingPanel.setBackground(new Color(230, 230, 230)); // Grey color for spacing
            panel.add(spacingPanel);
        }
    }


    private String[][] createSampleData() {
        String currentUser = User.currentUser;

        String followedUsers = UserRelationshipManager.getFollowers(currentUser);

        // Temporary structure to hold the data
        String[][] tempData = new String[100][]; // Assuming a maximum of 100 posts for simplicity
        int count = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("img", "image_details.txt"))) {
            String line;
            while ((line = reader.readLine()) != null && count < tempData.length) {
                if(line.contains("_$SEPARATOR$_")){
                    line = reader.readLine();

                    ReadImageDetails details = new ReadImageDetails();
                    details.loadDetails(line.split("\\_\\$separator\\$\\_")[0].split(": ")[1]);

                    String[] info = generatePostInfo(followedUsers, details);
                    if (followedUsers.contains(info[0]))
                        tempData[count++] = info;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Transfer the data to the final array
        String[][] sampleData = new String[count][];
        System.arraycopy(tempData, 0, sampleData, 0, count);

        return sampleData;
    }

    private String[] generatePostInfo(String followedUsers, ReadImageDetails details){
        String[] info = new String[4];

        if (followedUsers.contains(details.getUsername())) {
            String imagePath = "img/uploaded/" + details.getID() + ".png"; // Assuming PNG format
            String description = details.getBio();
            String likes = "Likes: " + details.getLikes();
            String imagePoster = details.getUsername();

            info = new String[]{imagePoster, description, likes, imagePath};
        }

        info[0] = details.getUsername();
        return info;
    }

    public void displayImage(String imagePath) throws IOException {
        uiWindow.clearWindow();
        new ImageViewerUI(imagePath, this);
    }
}


