package src.UI;
import static javax.swing.BoxLayout.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SignUpUI extends StartUI {
    private JTextField txtBio = new JTextField("");
    private JButton btnRegister;
    private final String profilePhotoStoragePath = "quack/img/storage/profile/";
    private JButton btnSignIn;
    private JLabel labelBio = new JLabel("Bio                   ");



    public SignUpUI() {
        uiWindow.clearWindow();
        uiWindow.setVisible(true);
        uiWindow.setTitle("Quackstagram - Register");
        initializeUI();
    }

    private void initializeUI() {
        createButtons();

        // Creating and Adding components to the frame
        uiWindow.add(header.getHeader(), BorderLayout.NORTH); // Header with the Register label
        uiWindow.add(createFieldsPanel(), BorderLayout.CENTER); // Text fields panel
        uiWindow.add(initializeRegisterPanel(), BorderLayout.SOUTH); //sign in and register buttons

        uiWindow.refresh();
    }



    private JPanel createFieldsPanel(){
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));


        formatFields(); //Format and populate the text fields

        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createProfilePicPanel());
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createUsernamePanel());
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createPasswordPanel());
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createBioPanel());

        return fieldsPanel;
    }


    private void formatFields(){

        txtBio.setForeground(Color.GRAY);
        txtUsername.setForeground(Color.GRAY);
        txtPassword.setForeground(Color.GRAY);

    }

    private JPanel initializeRegisterPanel(){
        JPanel registerPanel = new JPanel(new BorderLayout()); // Panel to contain the register button
        registerPanel.setBackground(Color.WHITE); // Background for the panel

        registerPanel.add(btnRegister, BorderLayout.CENTER);
        registerPanel.add(btnSignIn, BorderLayout.SOUTH);
        return registerPanel;
    }

    private void createButtons(){
        createBtnRegister();
        createBtnSignIn();
    }

    private void createBtnRegister(){
        // Register button with black text
        btnRegister = new JButton("Register");
        btnRegister.addActionListener(this::onRegisterClicked);
        btnRegister.setBackground(new Color(255, 90, 95)); // Use a red color that matches the mockup
        btnRegister.setForeground(Color.BLACK); // Set the text color to black
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void createBtnSignIn(){
        btnSignIn = new JButton("Already have an account? Sign In");
        btnSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignInUI();
            }
        });
    }


    private void onRegisterClicked(ActionEvent event) {
        String username = txtUsername.getText();
        String hashedPassword = hashPassword(txtPassword.getText());
        String bio = txtBio.getText();

        if(username.equals("") || txtPassword.equals("")){
            JOptionPane.showMessageDialog(null, "Fields cannot be empty", "Empty Fields", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            if (doesUsernameExist(username)) {
                JOptionPane.showMessageDialog(uiWindow, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {

                saveCredentials(username, hashedPassword, bio);


                handleProfilePictureUpload();
                openSignInUI();
            }
        }
    }
    
    private boolean doesUsernameExist(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsUsernamesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

     // Method to handle profile picture upload
     private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(uiWindow) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfilePicture(selectedFile, txtUsername.getText());
        }
    }

    private void saveProfilePicture(File file, String username) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + username + ".png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveCredentials(String username, String password, String bio) {
        try(BufferedWriter writerUsername = new BufferedWriter(new FileWriter(credentialsUsernamesFilePath, true));
            BufferedWriter writerPassword = new BufferedWriter(new FileWriter(credentialsPasswordsFilePath, true));
            BufferedWriter writerBio = new BufferedWriter(new FileWriter(userBiosFilePath, true)))
        {
            writerUsername.write(username);
            writerPassword.write(password);
            writerBio.write(bio);

            writerUsername.newLine();
            writerPassword.newLine();
            writerBio.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSignInUI() {
        // Close the SignUpUI frame
        uiWindow.clearWindow();

        // Open the SignInUI frame
        new SignInUI();

    }

    private JPanel createBioPanel(){
        JPanel bioPanel = new JPanel();
        bioPanel.setLayout(new BoxLayout(bioPanel, Y_AXIS));
        bioPanel.add(labelBio);
        bioPanel.add(txtBio);

        return bioPanel;
    }

}
