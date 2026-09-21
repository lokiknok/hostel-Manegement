package ui;

import dao.*;
import model.User;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DashboardFrame extends JFrame {
    private User currentUser;

    private StudentDAO studentDAO;
    private RoomDAO roomDAO;
    private FeeDAO feeDAO;
    private ComplaintDAO complaintDAO;

    private JPanel cardContainerPanel;
    private CardLayout cardLayout;

    // Sub-panels
    private JPanel dashboardHomePanel;
    private StudentPanel studentPanel;
    private RoomPanel roomPanel;
    private AllocationPanel allocationPanel;
    private FeePanel feePanel;
    private AttendancePanel attendancePanel;
    private ComplaintPanel complaintPanel;
    private VisitorPanel visitorPanel;
    private LeavePanel leavePanel;
    private ReportPanel reportPanel;

    // Summary Card Labels
    private JLabel lblTotalStudentsVal, lblTotalRoomsVal, lblAvailRoomsVal, lblOccupiedRoomsVal, lblPendingFeesVal, lblComplaintsVal;

    public DashboardFrame(User user) {
        this.currentUser = user;
        studentDAO = new StudentDAO();
        roomDAO = new RoomDAO();
        feeDAO = new FeeDAO();
        complaintDAO = new ComplaintDAO();

        initUI();
        refreshMetrics();
    }

    private void initUI() {
        setTitle("Hostel Management System - Admin Dashboard");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1024, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainLayout = new JPanel(new BorderLayout());

        // Top Header Bar
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(UIUtils.NAVY_DARK);
        topHeader.setPreferredSize(new Dimension(getWidth(), 60));
        topHeader.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblBrand = new JLabel("HOSTEL MANAGEMENT SYSTEM");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblUserRole = new JLabel("Logged in as: " + (currentUser != null ? currentUser.getFullName() : "Admin"));
        lblUserRole.setFont(UIUtils.FONT_REGULAR);
        lblUserRole.setForeground(new Color(203, 213, 225));

        topHeader.add(lblBrand, BorderLayout.WEST);
        topHeader.add(lblUserRole, BorderLayout.EAST);

        // Sidebar Navigation Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.NAVY_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(new EmptyBorder(15, 10, 15, 10));

        addSidebarButton(sidebar, "Dashboard", "DASHBOARD");
        addSidebarButton(sidebar, "Student Management", "STUDENTS");
        addSidebarButton(sidebar, "Room Management", "ROOMS");
        addSidebarButton(sidebar, "Room Allocation", "ALLOCATION");
        addSidebarButton(sidebar, "Fee Management", "FEES");
        addSidebarButton(sidebar, "Attendance", "ATTENDANCE");
        addSidebarButton(sidebar, "Complaints", "COMPLAINTS");
        addSidebarButton(sidebar, "Visitors", "VISITORS");
        addSidebarButton(sidebar, "Leave Management", "LEAVES");
        addSidebarButton(sidebar, "Reports", "REPORTS");

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = UIUtils.createStyledButton("LOGOUT", UIUtils.DANGER_RED, Color.WHITE);
        btnLogout.setMaximumSize(new Dimension(220, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        // Card Container Panel
        cardLayout = new CardLayout();
        cardContainerPanel = new JPanel(cardLayout);

        // Instantiate Sub-panels
        dashboardHomePanel = createDashboardHomePanel();
        studentPanel = new StudentPanel();
        roomPanel = new RoomPanel();
        allocationPanel = new AllocationPanel();
        feePanel = new FeePanel();
        attendancePanel = new AttendancePanel();
        complaintPanel = new ComplaintPanel();
        visitorPanel = new VisitorPanel();
        leavePanel = new LeavePanel(currentUser);
        reportPanel = new ReportPanel();

        cardContainerPanel.add(dashboardHomePanel, "DASHBOARD");
        cardContainerPanel.add(studentPanel, "STUDENTS");
        cardContainerPanel.add(roomPanel, "ROOMS");
        cardContainerPanel.add(allocationPanel, "ALLOCATION");
        cardContainerPanel.add(feePanel, "FEES");
        cardContainerPanel.add(attendancePanel, "ATTENDANCE");
        cardContainerPanel.add(complaintPanel, "COMPLAINTS");
        cardContainerPanel.add(visitorPanel, "VISITORS");
        cardContainerPanel.add(leavePanel, "LEAVES");
        cardContainerPanel.add(reportPanel, "REPORTS");

        mainLayout.add(topHeader, BorderLayout.NORTH);
        mainLayout.add(sidebar, BorderLayout.WEST);
        mainLayout.add(cardContainerPanel, BorderLayout.CENTER);

        add(mainLayout);
    }

    private void addSidebarButton(JPanel sidebar, String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setFont(UIUtils.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(UIUtils.NAVY_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIUtils.ACCENT_BLUE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIUtils.NAVY_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(cardContainerPanel, cardName);
            if ("DASHBOARD".equals(cardName)) {
                refreshMetrics();
            } else if ("STUDENTS".equals(cardName)) {
                studentPanel.loadStudentData();
            } else if ("ROOMS".equals(cardName)) {
                roomPanel.loadRoomData();
            } else if ("ALLOCATION".equals(cardName)) {
                allocationPanel.loadDropdowns();
                allocationPanel.loadAllocations();
            } else if ("FEES".equals(cardName)) {
                feePanel.loadStudentsDropdown();
                feePanel.loadFeeData();
            } else if ("ATTENDANCE".equals(cardName)) {
                attendancePanel.loadStudentsDropdown();
                attendancePanel.loadAttendanceData();
            } else if ("COMPLAINTS".equals(cardName)) {
                complaintPanel.loadStudentsDropdown();
                complaintPanel.loadComplaintData();
            } else if ("VISITORS".equals(cardName)) {
                visitorPanel.loadStudentsDropdown();
                visitorPanel.loadVisitorData();
            } else if ("LEAVES".equals(cardName)) {
                leavePanel.loadStudentsDropdown();
                leavePanel.loadLeaveData();
            } else if ("REPORTS".equals(cardName)) {
                reportPanel.loadSelectedReport();
            }
        });

        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private JPanel createDashboardHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIUtils.BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Title
        JLabel lblHeader = new JLabel("System Overview & Quick Stats");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.NAVY_DARK);

        // Metrics Grid (2 rows x 3 cols)
        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        metricsGrid.setOpaque(false);

        lblTotalStudentsVal = new JLabel("0");
        lblTotalRoomsVal = new JLabel("0");
        lblAvailRoomsVal = new JLabel("0");
        lblOccupiedRoomsVal = new JLabel("0");
        lblPendingFeesVal = new JLabel("$0.00");
        lblComplaintsVal = new JLabel("0");

        metricsGrid.add(createCard("TOTAL STUDENTS", lblTotalStudentsVal, UIUtils.ACCENT_BLUE));
        metricsGrid.add(createCard("TOTAL ROOMS", lblTotalRoomsVal, UIUtils.NAVY_DARK));
        metricsGrid.add(createCard("AVAILABLE ROOMS", lblAvailRoomsVal, UIUtils.SUCCESS_GREEN));
        metricsGrid.add(createCard("OCCUPIED ROOMS", lblOccupiedRoomsVal, UIUtils.WARNING_AMBER));
        metricsGrid.add(createCard("PENDING FEES", lblPendingFeesVal, UIUtils.DANGER_RED));
        metricsGrid.add(createCard("TOTAL COMPLAINTS", lblComplaintsVal, new Color(147, 51, 234)));

        // Quick Action Cards
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        quickActions.setBackground(Color.WHITE);
        quickActions.setBorder(BorderFactory.createTitledBorder("Quick Operations"));

        JButton btnQuickAddStu = UIUtils.createStyledButton("+ Add Student", UIUtils.ACCENT_BLUE, Color.WHITE);
        JButton btnQuickAlloc = UIUtils.createStyledButton("+ Allocate Room", UIUtils.SUCCESS_GREEN, Color.WHITE);
        JButton btnQuickFee = UIUtils.createStyledButton("+ Record Payment", UIUtils.WARNING_AMBER, Color.WHITE);
        JButton btnQuickRep = UIUtils.createStyledButton("View Reports", UIUtils.NAVY_DARK, Color.WHITE);

        btnQuickAddStu.addActionListener(e -> cardLayout.show(cardContainerPanel, "STUDENTS"));
        btnQuickAlloc.addActionListener(e -> cardLayout.show(cardContainerPanel, "ALLOCATION"));
        btnQuickFee.addActionListener(e -> cardLayout.show(cardContainerPanel, "FEES"));
        btnQuickRep.addActionListener(e -> cardLayout.show(cardContainerPanel, "REPORTS"));

        quickActions.add(btnQuickAddStu);
        quickActions.add(btnQuickAlloc);
        quickActions.add(btnQuickFee);
        quickActions.add(btnQuickRep);

        panel.add(lblHeader, BorderLayout.NORTH);
        panel.add(metricsGrid, BorderLayout.CENTER);
        panel.add(quickActions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(String title, JLabel valLabel, Color borderAccent) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 6, 0, 0, borderAccent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
            )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtils.FONT_SMALL);
        lblTitle.setForeground(UIUtils.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLabel.setForeground(UIUtils.NAVY_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshMetrics() {
        int totalStudents = studentDAO.getTotalStudentCount();
        int totalRooms = roomDAO.getTotalRoomsCount();
        int availRooms = roomDAO.getAvailableRoomsCount();
        int occupiedRooms = roomDAO.getOccupiedRoomsCount();
        double pendingFees = feeDAO.getTotalPendingFeesSum();
        int totalComplaints = complaintDAO.getTotalComplaintsCount();

        lblTotalStudentsVal.setText(String.valueOf(totalStudents));
        lblTotalRoomsVal.setText(String.valueOf(totalRooms));
        lblAvailRoomsVal.setText(String.valueOf(availRooms));
        lblOccupiedRoomsVal.setText(String.valueOf(occupiedRooms));
        lblPendingFeesVal.setText(NumberFormat.getCurrencyInstance(Locale.US).format(pendingFees));
        lblComplaintsVal.setText(String.valueOf(totalComplaints));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}

