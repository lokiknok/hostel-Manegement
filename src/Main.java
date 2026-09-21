import db.DatabaseConnection;
import ui.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set Native/System Look and Feel for clean modern look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Launch Application UI on EDT
        SwingUtilities.invokeLater(() -> {
            boolean connected = DatabaseConnection.testConnection();
            if (!connected) {
                System.out.println("[Warning] MySQL database connection could not be established. Check DatabaseConnection.java settings & MySQL service.");
            }
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

