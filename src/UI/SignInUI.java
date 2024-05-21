package src.UI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import src.DataStorage.SQLDBConnection;
import src.DataStorage.User;


public class SignInUI extends StartUI{
    private JButton btnSignIn, btnRegisterNow;
    private User newUser;

    public SignInUI() {
        uiWindow.clearWindow();
        uiWindow.setVisible(true);
        uiWindow.setTitle("Quackstagram - Log In");
        initializeUI();
    }

    private void initializeUI() {

        //Create sign in and sign up buttons
        createButtons();

        // Creating and adding components to the frame
        uiWindow.add(header.getHeader(), BorderLayout.NORTH); //Header with the Register label
        uiWindow.add(createFieldsPanel(), BorderLayout.CENTER); // Text fields panel
        uiWindow.add(createRegisterPanel(), BorderLayout.SOUTH); //Panel to contain the register button

        // Creating and adding the button panel to the frame
        uiWindow.add(createButtonPanel(), BorderLayout.SOUTH);

        uiWindow.refresh();

    }


    private JPanel createFieldsPanel(){
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        txtUsername.setForeground(Color.GRAY);
        txtPassword.setForeground(Color.GRAY);

        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createProfilePicPanel()); // Profile picture placeholder without border
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createUsernamePanel());
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createPasswordPanel());
        fieldsPanel.add(Box.createVerticalStrut(10));

        return fieldsPanel;
    }

    private JPanel createRegisterPanel(){
        JPanel registerPanel = new JPanel(new BorderLayout()); // Panel to contain the register button
        registerPanel.setBackground(Color.WHITE); // Background for the panel

        registerPanel.add(btnSignIn, BorderLayout.CENTER);

        return registerPanel;
    }

    private JPanel createButtonPanel(){

        // Panel to hold both sign in and sign up buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Grid layout with 1 row, 2 columns
        buttonPanel.setBackground(Color.white);

        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow); // New button for navigating to SignUpUI

        return buttonPanel;
    }

    private void createButtons(){
        createBtnSignIn();
        createBtnRegisterNow();
    }

    private void createBtnRegisterNow(){
        btnRegisterNow = new JButton("No Account? Register Now");
        btnRegisterNow.addActionListener(this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE); // Set a different color for distinction
        btnRegisterNow.setForeground(Color.BLACK);
        btnRegisterNow.setFocusPainted(false);
        btnRegisterNow.setBorderPainted(false);
    }


    private void createBtnSignIn(){
        btnSignIn = new JButton("Sign-In");
        btnSignIn.addActionListener(this::onSignInClicked);
        btnSignIn.setBackground(new Color(255, 90, 95)); // Use a red color that matches the mockup
        btnSignIn.setForeground(Color.BLACK); // Set the text color to black
        btnSignIn.setFocusPainted(false);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
    }


   private void onSignInClicked(ActionEvent event) {
    String enteredUsername = txtUsername.getText();
    String enteredPassword = txtPassword.getText();

    if(enteredUsername.equals("") || enteredPassword.equals("")){
        JOptionPane.showMessageDialog(null, "Fields cannot be empty", "Empty Fields", JOptionPane.INFORMATION_MESSAGE);
    }
    else{
        if (verifyCredentials(enteredUsername, enteredPassword)) {
            // Close the SignUpUI frame
            uiWindow.clearWindow();
            User.setCurrentUserName(enteredUsername);

            // Open the SignInUI frame
            SwingUtilities.invokeLater(() -> {
                new InstagramProfileUI(newUser);
            });
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect username and/or password.", "Failed to Login", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

private void onRegisterNowClicked(ActionEvent event) {
    // Close the SignInUI frame
    uiWindow.clearWindow();

    // Open the SignUpUI frame
        new SignUpUI();
}

private boolean verifyCredentials(String username, String password) {
    String query = "SELECT password, bio FROM Users WHERE username = ?";
    try(Connection connection = SQLDBConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){


        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String storedHashedPassword = resultSet.getString("password");
            String bio = resultSet.getString("bio");
            String hashedPassword = hashPassword(password);

            if (storedHashedPassword.equals(hashedPassword)) {
                // Create User object and save information
                newUser = new User(username, bio, storedHashedPassword);
                saveUserInformation(newUser);

                return true;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}



   private void saveUserInformation(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("quack/data/users.txt", false))) {
            writer.write(user.toString());  // Implement a suitable toString method in User class
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignInUI signInUI = new SignInUI();
        });
    }


}
