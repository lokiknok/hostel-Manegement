package ui;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Hostel Management System - Login");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIUtils.BG_LIGHT);

        // Header Banner
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(UIUtils.NAVY_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("HOSTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Admin Portal Login", SwingConstants.CENTER);
        lblSub.setFont(UIUtils.FONT_REGULAR);
        lblSub.setForeground(new Color(203, 213, 225));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UIUtils.FONT_BOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(UIUtils.FONT_REGULAR);
        txtUsername.setText("admin"); // Default convenience
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UIUtils.FONT_BOLD);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(UIUtils.FONT_REGULAR);
        txtPassword.setText("admin123"); // Default convenience
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);

        btnLogin = UIUtils.createStyledButton("LOGIN", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnExit = UIUtils.createStyledButton("EXIT", UIUtils.DANGER_RED, Color.WHITE);

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event Handlers
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));
        
        // Enter key shortcut
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome back, " + user.getFullName() + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close login frame
            SwingUtilities.invokeLater(() -> new DashboardFrame(user).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!\n\nNote: If MySQL DB is not connected yet, ensure database.sql is imported.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

