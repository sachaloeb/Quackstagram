package src;
import javax.swing.SwingUtilities;

import src.UI.SignInUI;
import src.UI.SingletonUiWindow;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            SingletonUiWindow uiWindow = SingletonUiWindow.getInstance();
            uiWindow.setVisible(true);
            new SignInUI();

        });
    }
}
