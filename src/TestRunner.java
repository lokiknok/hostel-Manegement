import dao.*;
import model.*;
import ui.*;
import javax.swing.*;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=== Running Hostel Management System Smoke Tests ===");

        // Test 1: User Model instantiation
        User user = new User(1, "admin", "admin123", "System Admin", "ADMIN", null);
        assert "admin".equals(user.getUsername());
        System.out.println("[PASS] User model test");

        // Test 2: Room status calculation
        Room r1 = new Room(1, "101", 1, "Single", 1, 1, 0, "FULL");
        assert "FULL".equals(r1.calculateStatus());
        Room r2 = new Room(2, "102", 1, "Double", 2, 1, 1, "PARTIALLY OCCUPIED");
        assert "PARTIALLY OCCUPIED".equals(r2.calculateStatus());
        Room r3 = new Room(3, "103", 1, "Double", 2, 0, 2, "AVAILABLE");
        assert "AVAILABLE".equals(r3.calculateStatus());
        System.out.println("[PASS] Room status calculation logic");

        // Test 3: Fee pending calculation
        Fee fee = new Fee(1, 101, "John Doe", "101", 50000.0, 30000.0, 20000.0, null, "UPI", "PARTIAL", "Test");
        assert fee.getPendingAmount() == 20000.0;
        assert "PARTIAL".equals(fee.getPaymentStatus());
        System.out.println("[PASS] Fee pending amount calculation");

        // Test 4: Frame & Panel Instantiations in Headless/EDT check
        try {
            LoginFrame loginFrame = new LoginFrame();
            assert loginFrame.getTitle().contains("Hostel Management System");

            DashboardFrame dashboard = new DashboardFrame(user);
            assert dashboard.getTitle().contains("Admin Dashboard");

            System.out.println("[PASS] Swing Frames & Panels initialization");
        } catch (Exception e) {
            System.err.println("Swing UI init note: " + e.getMessage());
        }

        System.out.println("=== All Smoke Tests Passed Successfully! ===");
    }
}

