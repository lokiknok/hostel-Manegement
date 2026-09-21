package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hostel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "admin"; // Default password - update as needed for local setup

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please include mysql-connector-j jar in classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void showConnectionError() {
        JOptionPane.showMessageDialog(
            null,
            "Could not connect to MySQL Database!\n\n" +
            "Please check:\n" +
            "1. MySQL service is running on port 3306.\n" +
            "2. Database 'hostel_management' exists (run database.sql).\n" +
            "3. Database credentials in src/db/DatabaseConnection.java are correct.",
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

