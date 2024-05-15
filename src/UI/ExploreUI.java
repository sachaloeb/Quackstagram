package src.UI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import src.UI.UIComponent.DefaultHeader;
import src.UI.UIComponent.NavigationBar;


public class ExploreUI{

    private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int IMAGE_SIZE = WIDTH / 3; // Size for each image in the grid

    private final NavigationBar navigationBar = new NavigationBar();

    private final DefaultHeader header = new DefaultHeader(" Explore 🐥");

    public ExploreUI() {
        initializeUI();
    }

    private void initializeUI() {

        uiWindow.clearWindow();

        uiWindow.setLayout(new BorderLayout()); // Reset the layout manager

        JPanel headerPanel = header.getHeader();

        //navigation bar
        JPanel navigationPanel = navigationBar.getNavigationBar();

        JPanel mainContentPanel = createMainContentPanel();

        // Add panels to the frame
        uiWindow.add(headerPanel, BorderLayout.NORTH);
        uiWindow.add(mainContentPanel, BorderLayout.CENTER);
        uiWindow.add(navigationPanel, BorderLayout.SOUTH);

        uiWindow.refresh();

        
    }
    
    private JPanel createMainContentPanel() {

        // Create the main content panel with search and image grid
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));

        // Search bar at the top
        JPanel searchPanel = createSearchPanel();

       // Image Grid
        JPanel imageGridPanel = createImageGridPanel();

        // Scroll Panel
        JScrollPane scrollPane = createScrollPane(imageGridPanel);

        // Main content panel that holds both the search bar and the image grid
        mainContentPanel.add(searchPanel);
        mainContentPanel.add(scrollPane); // This will stretch to take up remaining space
        return mainContentPanel;
}

    private JPanel createSearchPanel(){
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(" Search Users");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height)); // Limit the height
        return searchPanel;
    }

    private JPanel createImageGridPanel(){
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2)); // 3 columns, auto rows

        // Load images from the uploaded folder
        File imageDir = new File("quack/img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] imageFiles = imageDir.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg)"));
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {

                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageFile.getPath()).getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            displayImage(imageFile.getPath()); // Call method to display the clicked image
                        }
                    });
                    imageGridPanel.add(imageLabel);
                }
            }
        }

        return imageGridPanel;
    }

    private JScrollPane createScrollPane(JPanel panel){
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

   private void displayImage(String imagePath) {
        uiWindow.clearWindow();
        ImageViewerUI imageViewer = new ImageViewerUI(imagePath, this);
}
}