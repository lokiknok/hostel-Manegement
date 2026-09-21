package ui;

import dao.AttendanceDAO;
import dao.StudentDAO;
import model.Attendance;
import model.Student;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AttendancePanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private StudentDAO studentDAO;

    private JTextField txtAttendanceId, txtDate, txtCheckIn, txtCheckOut, txtSearchStudent, txtFilterDate;
    private JComboBox<Student> cbStudents;
    private JComboBox<String> cbStatus;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    private JButton btnMark, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;

    public AttendancePanel() {
        attendanceDAO = new AttendanceDAO();
        studentDAO = new StudentDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadStudentsDropdown();
        loadAttendanceData();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Attendance Management");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.NAVY_DARK);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtAttendanceId = new JTextField(); txtAttendanceId.setEditable(false);
        cbStudents = new JComboBox<>();
        txtDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtCheckIn = new JTextField("08:00:00");
        txtCheckOut = new JTextField("21:00:00");
        cbStatus = new JComboBox<>(new String[]{"PRESENT", "ABSENT", "LEAVE"});

        addFormField(formPanel, gbc, 0, "Attendance ID:", txtAttendanceId);
        addFormField(formPanel, gbc, 1, "Select Student*:", cbStudents);
        addFormField(formPanel, gbc, 2, "Date (YYYY-MM-DD)*:", txtDate);
        addFormField(formPanel, gbc, 3, "Check-in Time (HH:MM:SS):", txtCheckIn);
        addFormField(formPanel, gbc, 4, "Check-out Time (HH:MM:SS):", txtCheckOut);
        addFormField(formPanel, gbc, 5, "Attendance Status*:", cbStatus);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        btnMark = UIUtils.createStyledButton("Mark Attendance", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnUpdate = UIUtils.createStyledButton("Update", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Delete", UIUtils.DANGER_RED, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Clear", UIUtils.TEXT_MUTED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);

        btnPanel.add(btnMark);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setPreferredSize(new Dimension(380, 0));
        formScroll.setBorder(null);

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        txtSearchStudent = new JTextField(12);
        txtFilterDate = new JTextField(10);
        btnSearch = UIUtils.createStyledButton("Filter / Search", UIUtils.ACCENT_BLUE, Color.WHITE);

        searchPanel.add(new JLabel("Student:"));
        searchPanel.add(txtSearchStudent);
        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(txtFilterDate);
        searchPanel.add(btnSearch);

        // JTable
        String[] cols = {"ID", "Student Name", "Date", "Check-in", "Check-out", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        attendanceTable = new JTable(tableModel);
        UIUtils.styleTable(attendanceTable);

        JScrollPane tableScroll = new JScrollPane(attendanceTable);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Handlers
        btnMark.addActionListener(e -> markAttendance());
        btnUpdate.addActionListener(e -> updateAttendance());
        btnDelete.addActionListener(e -> deleteAttendance());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadStudentsDropdown();
            loadAttendanceData();
        });
        btnSearch.addActionListener(e -> searchAttendance());

        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && attendanceTable.getSelectedRow() != -1) {
                populateFormFromTable(attendanceTable.getSelectedRow());
            }
        });
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_SMALL);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.6;
        panel.add(comp, gbc);
    }

    public void loadStudentsDropdown() {
        cbStudents.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudents.addItem(s);
        }
    }

    public void loadAttendanceData() {
        tableModel.setRowCount(0);
        List<Attendance> list = attendanceDAO.getAllAttendance();
        for (Attendance a : list) {
            tableModel.addRow(new Object[]{
                a.getAttendanceId(), a.getStudentName(), a.getAttendanceDate(),
                a.getCheckInTime() != null ? a.getCheckInTime().toString() : "-",
                a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "-",
                a.getStatus()
            });
        }
    }

    private void searchAttendance() {
        String stuTerm = txtSearchStudent.getText().trim();
        String dateStr = txtFilterDate.getText().trim();
        Date filterDate = null;
        if (!dateStr.isEmpty()) {
            try { filterDate = Date.valueOf(dateStr); } catch (Exception ignored) {}
        }

        tableModel.setRowCount(0);
        List<Attendance> list = attendanceDAO.searchAttendance(stuTerm, filterDate);
        for (Attendance a : list) {
            tableModel.addRow(new Object[]{
                a.getAttendanceId(), a.getStudentName(), a.getAttendanceDate(),
                a.getCheckInTime() != null ? a.getCheckInTime().toString() : "-",
                a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "-",
                a.getStatus()
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtAttendanceId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtDate.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtCheckIn.setText(String.valueOf(tableModel.getValueAt(row, 3)).equals("-") ? "" : String.valueOf(tableModel.getValueAt(row, 3)));
        txtCheckOut.setText(String.valueOf(tableModel.getValueAt(row, 4)).equals("-") ? "" : String.valueOf(tableModel.getValueAt(row, 4)));
        cbStatus.setSelectedItem(tableModel.getValueAt(row, 5));
    }

    private void markAttendance() {
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Attendance a = new Attendance();
            a.setStudentId(s.getStudentId());
            a.setAttendanceDate(Date.valueOf(txtDate.getText().trim()));
            String inTimeStr = txtCheckIn.getText().trim();
            String outTimeStr = txtCheckOut.getText().trim();

            if (!inTimeStr.isEmpty()) a.setCheckInTime(Time.valueOf(inTimeStr));
            if (!outTimeStr.isEmpty()) a.setCheckOutTime(Time.valueOf(outTimeStr));
            a.setStatus((String) cbStatus.getSelectedItem());

            if (attendanceDAO.addAttendance(a)) {
                JOptionPane.showMessageDialog(this, "Attendance marked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendanceData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark attendance.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format! Use Date: YYYY-MM-DD, Time: HH:MM:SS.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAttendance() {
        if (txtAttendanceId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an attendance record from table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) return;

        try {
            Attendance a = new Attendance();
            a.setAttendanceId(Integer.parseInt(txtAttendanceId.getText()));
            a.setStudentId(s.getStudentId());
            a.setAttendanceDate(Date.valueOf(txtDate.getText().trim()));
            String inTimeStr = txtCheckIn.getText().trim();
            String outTimeStr = txtCheckOut.getText().trim();

            if (!inTimeStr.isEmpty() && !inTimeStr.equals("-")) a.setCheckInTime(Time.valueOf(inTimeStr));
            if (!outTimeStr.isEmpty() && !outTimeStr.equals("-")) a.setCheckOutTime(Time.valueOf(outTimeStr));
            a.setStatus((String) cbStatus.getSelectedItem());

            if (attendanceDAO.updateAttendance(a)) {
                JOptionPane.showMessageDialog(this, "Attendance record updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendanceData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update attendance.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format!", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAttendance() {
        if (txtAttendanceId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an attendance record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this attendance entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtAttendanceId.getText());
            if (attendanceDAO.deleteAttendance(id)) {
                JOptionPane.showMessageDialog(this, "Attendance entry deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendanceData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete entry.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtAttendanceId.setText("");
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtCheckIn.setText("08:00:00");
        txtCheckOut.setText("21:00:00");
        cbStatus.setSelectedIndex(0);
        attendanceTable.clearSelection();
    }
}

