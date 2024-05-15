package src.UI;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.ImageScalor;
import src.UI.UIComponent.DefaultHeader;
import src.UI.UIComponent.NavigationBar;

public class ImageUploadUI{

    private static SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();
    private JLabel imagePreviewLabel;
    private JTextArea bioTextArea;
    private JButton uploadButton;
    private JButton saveButton;
    private boolean imageUploaded = false;

    private final NavigationBar navigationBar = new NavigationBar();
    private final DefaultHeader header = new DefaultHeader(" Upload Image 🐥");

    public ImageUploadUI() {
        uiWindow.clearWindow();
        uiWindow.setTitle("Upload Image");
        initializeUI();
    }

    private void initializeUI() {
        JPanel headerPanel = header.getHeader();
        JPanel navigationPanel = navigationBar.getNavigationBar();
        JPanel contentPanel = createContentPanel();

        // Save button (for bio)
        saveButton = new JButton("Save Caption");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(this::saveBioAction);

        // Add panels to frame
        uiWindow.add(headerPanel, BorderLayout.NORTH);
        uiWindow.add(contentPanel, BorderLayout.CENTER);
        uiWindow.add(navigationPanel, BorderLayout.SOUTH);

        uiWindow.refresh();
    }

    private JPanel createContentPanel(){
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Image preview
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePreviewLabel.setPreferredSize(new Dimension(uiWindow.getWidth(), (uiWindow.getHeight() / 3)));
        // Set an initial empty icon to the imagePreviewLabel
        ImageIcon emptyImageIcon = new ImageIcon();
        imagePreviewLabel.setIcon(emptyImageIcon);

        // Bio text area
        bioTextArea = new JTextArea("Enter a caption");
        bioTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioTextArea);
        bioScrollPane.setPreferredSize(new Dimension(uiWindow.getWidth() - 50, (uiWindow.getHeight() / 6)));

        // Upload button
        uploadButton = new JButton("Upload Image");
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.addActionListener(this::uploadAction);


        contentPanel.add(imagePreviewLabel);
        contentPanel.add(bioScrollPane);
        contentPanel.add(uploadButton);
        return contentPanel;
    }

    private void uploadAction(ActionEvent event) {
        File file = chooseFile();
        if(file != null) {
            try {
                String username = readUsername(); // Read username from users.txt
                int imageId = getNextImageId(username);
                String fileExtension = getFileExtension(file);
                String newFileName = username + "_" + imageId + "." + fileExtension;
    
                Path destPath = Paths.get("quack/img", "uploaded", newFileName);
                Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
    
                // Save the bio and image ID to a text file
                saveImageInfo(username + "_" + imageId, username, bioTextArea.getText());
    
                // Load the image from the saved path
                ImageIcon imageIcon = new ImageIcon(destPath.toString());
    
                // Check if imagePreviewLabel has a valid size
                if (imagePreviewLabel.getWidth() > 0 && imagePreviewLabel.getHeight() > 0) {
                    Image image = imageIcon.getImage();

                    // Set the image icon with the scaled image
                    imageIcon.setImage(ImageScalor.scaleImage(image, imagePreviewLabel.getWidth(), imagePreviewLabel.getHeight()));
                }
    
                imagePreviewLabel.setIcon(imageIcon);
    
                // Update the flag to indicate that an image has been uploaded
                imageUploaded = true;
    
                // Change the text of the upload button
                uploadButton.setText("Upload Another Image");
    
                JOptionPane.showMessageDialog(uiWindow, "Image uploaded and preview updated!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(uiWindow, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private File chooseFile(){
        File selectedFile;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(filter);
    
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION){
            selectedFile = fileChooser.getSelectedFile();
        }
        else{
            selectedFile = null;
        }
        
        return selectedFile;
    }
    
    private int getNextImageId(String username) throws IOException {
        Path storageDir = Paths.get("quack/img", "uploaded"); // Ensure this is the directory where images are saved
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    
        int maxId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDir, username + "_*")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(username.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore filenames that do not have a valid numeric ID
                    }
                }
            }
        }
        return maxId + 1; // Return the next available ID
    }
    
    private void saveImageInfo(String imageId, String username, String bio) throws IOException {
        Path infoFilePath = Paths.get("quack/img", "image_details.txt");
        if (!Files.exists(infoFilePath)) {
            Files.createFile(infoFilePath);
        }
    
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
        try (BufferedWriter writer = Files.newBufferedWriter(infoFilePath, StandardOpenOption.APPEND)) {
            writer.write("_$SEPARATOR$_\n");
            writer.write(String.format("ImageID: %s_$separator$_ Username: %s_$separator$_ Bio: %s_$separator$_ Timestamp: %s_$separator$_ Likes: 0", imageId, username, bio, timestamp));
            writer.newLine();
        }
}


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private void saveBioAction(ActionEvent event) {
        // Here you would handle saving the bio text
        String bioText = bioTextArea.getText();
        // For example, save the bio text to a file or database
        JOptionPane.showMessageDialog(uiWindow, "Caption saved: " + bioText);
    }

   private String readUsername() throws IOException {
    Path usersFilePath = Paths.get("quack/data", "users.txt");
    try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
        String line = reader.readLine();
        if (line != null) {
            return line.split(":")[0]; // Extract the username from the first line
        }
    }
    return null; // Return null if no username is found
}
}
