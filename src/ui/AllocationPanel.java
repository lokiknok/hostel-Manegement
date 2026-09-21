package ui;

import dao.RoomAllocationDAO;
import dao.RoomDAO;
import dao.StudentDAO;
import model.Room;
import model.RoomAllocation;
import model.Student;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AllocationPanel extends JPanel {
    private RoomAllocationDAO allocationDAO;
    private StudentDAO studentDAO;
    private RoomDAO roomDAO;

    private JComboBox<Student> cbStudents;
    private JComboBox<Room> cbRooms;
    private JComboBox<String> cbBeds;
    private JTextField txtAllocationDate;
    private JTable allocationTable;
    private DefaultTableModel tableModel;

    private JButton btnAllocate, btnChangeRoom, btnVacate, btnRefresh;

    public AllocationPanel() {
        allocationDAO = new RoomAllocationDAO();
        studentDAO = new StudentDAO();
        roomDAO = new RoomDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadDropdowns();
        loadAllocations();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Student Room Allocation");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.NAVY_DARK);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        // Control Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbStudents = new JComboBox<>();
        cbRooms = new JComboBox<>();
        cbBeds = new JComboBox<>(new String[]{"Bed-1", "Bed-2", "Bed-3", "Bed-4"});
        txtAllocationDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        addControlField(controlPanel, gbc, 0, "Select Student:", cbStudents);
        addControlField(controlPanel, gbc, 1, "Select Available Room:", cbRooms);
        addControlField(controlPanel, gbc, 2, "Select Bed Number:", cbBeds);
        addControlField(controlPanel, gbc, 3, "Allocation Date (YYYY-MM-DD):", txtAllocationDate);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setOpaque(false);

        btnAllocate = UIUtils.createStyledButton("Allocate Room", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnChangeRoom = UIUtils.createStyledButton("Change Room", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnVacate = UIUtils.createStyledButton("Vacate Room", UIUtils.DANGER_RED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);

        btnPanel.add(btnAllocate);
        btnPanel.add(btnChangeRoom);
        btnPanel.add(btnVacate);
        btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        controlPanel.add(btnPanel, gbc);

        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setPreferredSize(new Dimension(400, 0));
        controlScroll.setBorder(null);

        // Allocations Table
        String[] cols = {"Alloc ID", "Student Name", "Room Number", "Bed No", "Allocation Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        allocationTable = new JTable(tableModel);
        UIUtils.styleTable(allocationTable);

        JScrollPane tableScroll = new JScrollPane(allocationTable);

        add(headerPanel, BorderLayout.NORTH);
        add(controlScroll, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);

        // Event Handlers
        btnAllocate.addActionListener(e -> allocateStudentRoom());
        btnChangeRoom.addActionListener(e -> changeStudentRoom());
        btnVacate.addActionListener(e -> vacateStudentRoom());
        btnRefresh.addActionListener(e -> {
            loadDropdowns();
            loadAllocations();
        });
    }

    private void addControlField(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_SMALL);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.6;
        panel.add(comp, gbc);
    }

    public void loadDropdowns() {
        cbStudents.removeAllItems();
        cbRooms.removeAllItems();

        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudents.addItem(s);
        }

        List<Room> rooms = roomDAO.getAvailableRooms();
        for (Room r : rooms) {
            cbRooms.addItem(r);
        }
    }

    public void loadAllocations() {
        tableModel.setRowCount(0);
        List<RoomAllocation> list = allocationDAO.getAllAllocations();
        for (RoomAllocation a : list) {
            tableModel.addRow(new Object[]{
                a.getAllocationId(), a.getStudentName(), a.getRoomNumber(),
                a.getBedNumber(), a.getAllocationDate(), a.getStatus()
            });
        }
    }

    private void allocateStudentRoom() {
        Student student = (Student) cbStudents.getSelectedItem();
        Room room = (Room) cbRooms.getSelectedItem();
        String bed = (String) cbBeds.getSelectedItem();
        String dateStr = txtAllocationDate.getText().trim();

        if (student == null || room == null) {
            JOptionPane.showMessageDialog(this, "Please select both a student and an available room.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date date = Date.valueOf(dateStr);
            if (allocationDAO.allocateRoom(student.getStudentId(), room.getRoomId(), bed, date)) {
                JOptionPane.showMessageDialog(this, "Room allocated successfully to " + student.getName() + "!", "Allocation Success", JOptionPane.INFORMATION_MESSAGE);
                loadDropdowns();
                loadAllocations();
            } else {
                JOptionPane.showMessageDialog(this, "Allocation failed!\nStudent may already have an active room allocation or the room is full.", "Allocation Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeStudentRoom() {
        Student student = (Student) cbStudents.getSelectedItem();
        Room newRoom = (Room) cbRooms.getSelectedItem();
        String bed = (String) cbBeds.getSelectedItem();
        String dateStr = txtAllocationDate.getText().trim();

        if (student == null || newRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a student and the new room.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date date = Date.valueOf(dateStr);
            if (allocationDAO.changeRoom(student.getStudentId(), newRoom.getRoomId(), bed, date)) {
                JOptionPane.showMessageDialog(this, "Room changed successfully for " + student.getName() + "!", "Change Success", JOptionPane.INFORMATION_MESSAGE);
                loadDropdowns();
                loadAllocations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to change room. Ensure student has an active allocation and target room is available.", "Change Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void vacateStudentRoom() {
        Student student = (Student) cbStudents.getSelectedItem();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to vacate.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to vacate room for " + student.getName() + "?", "Confirm Vacate", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (allocationDAO.vacateRoom(student.getStudentId())) {
                JOptionPane.showMessageDialog(this, "Room vacated successfully!", "Vacate Success", JOptionPane.INFORMATION_MESSAGE);
                loadDropdowns();
                loadAllocations();
            } else {
                JOptionPane.showMessageDialog(this, "Vacate failed! Student does not have an active room allocation.", "Vacate Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

