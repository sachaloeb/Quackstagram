package src.UI.UIComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class DefaultHeader {

    private static int width = 300;
    private JPanel headerPanel;

    public DefaultHeader(String headerText){
        createHeaderPanel(headerText);
    }

    public void createHeaderPanel(String headerText){
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
         JLabel lblRegister = new JLabel(headerText);
         lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
         lblRegister.setForeground(Color.WHITE); // Set the text color to white
         headerPanel.add(lblRegister);
         headerPanel.setPreferredSize(new Dimension(width, 40)); // Give the header a fixed height
    }

    public JPanel getHeader(){
        return headerPanel;
    }
}
