package ui;

import dao.LeaveDAO;
import dao.StudentDAO;
import model.Leave;
import model.Student;
import model.User;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LeavePanel extends JPanel {
    private LeaveDAO leaveDAO;
    private StudentDAO studentDAO;
    private User currentUser;

    private JTextField txtLeaveId, txtLeaveFrom, txtLeaveTo, txtReason, txtApprovedBy, txtSearch;
    private JComboBox<Student> cbStudents;
    private JComboBox<String> cbStatus;
    private JTable leaveTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnApprove, btnReject, btnDelete, btnClear, btnSearch, btnRefresh;

    public LeavePanel(User currentUser) {
        this.currentUser = currentUser;
        leaveDAO = new LeaveDAO();
        studentDAO = new StudentDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadStudentsDropdown();
        loadLeaveData();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Student Leave Management");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.NAVY_DARK);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtLeaveId = new JTextField(); txtLeaveId.setEditable(false);
        cbStudents = new JComboBox<>();
        txtLeaveFrom = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtLeaveTo = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtReason = new JTextField();
        cbStatus = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED"});
        txtApprovedBy = new JTextField(currentUser != null ? currentUser.getUsername() : "admin"); txtApprovedBy.setEditable(false);

        addFormField(formPanel, gbc, 0, "Leave ID:", txtLeaveId);
        addFormField(formPanel, gbc, 1, "Select Student*:", cbStudents);
        addFormField(formPanel, gbc, 2, "Leave From (YYYY-MM-DD)*:", txtLeaveFrom);
        addFormField(formPanel, gbc, 3, "Leave To (YYYY-MM-DD)*:", txtLeaveTo);
        addFormField(formPanel, gbc, 4, "Reason*:", txtReason);
        addFormField(formPanel, gbc, 5, "Status:", cbStatus);
        addFormField(formPanel, gbc, 6, "Approved By:", txtApprovedBy);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = UIUtils.createStyledButton("Submit Leave", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnApprove = UIUtils.createStyledButton("Approve", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnReject = UIUtils.createStyledButton("Reject", UIUtils.DANGER_RED, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Clear", UIUtils.TEXT_MUTED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Delete", UIUtils.TEXT_DARK, Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnApprove);
        btnPanel.add(btnReject);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnDelete);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
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

        txtSearch = new JTextField(20);
        btnSearch = UIUtils.createStyledButton("Search", UIUtils.ACCENT_BLUE, Color.WHITE);

        searchPanel.add(new JLabel("Search Leaves:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // JTable
        String[] cols = {"ID", "Student Name", "Leave From", "Leave To", "Reason", "Status", "Approved By"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        leaveTable = new JTable(tableModel);
        UIUtils.styleTable(leaveTable);

        JScrollPane tableScroll = new JScrollPane(leaveTable);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Handlers
        btnAdd.addActionListener(e -> addLeave());
        btnApprove.addActionListener(e -> updateStatus("APPROVED"));
        btnReject.addActionListener(e -> updateStatus("REJECTED"));
        btnDelete.addActionListener(e -> deleteLeave());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadStudentsDropdown();
            loadLeaveData();
        });
        btnSearch.addActionListener(e -> searchLeave());

        leaveTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && leaveTable.getSelectedRow() != -1) {
                populateFormFromTable(leaveTable.getSelectedRow());
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

    public void loadLeaveData() {
        tableModel.setRowCount(0);
        List<Leave> list = leaveDAO.getAllLeaves();
        for (Leave l : list) {
            tableModel.addRow(new Object[]{
                l.getLeaveId(), l.getStudentName(), l.getLeaveFrom(),
                l.getLeaveTo(), l.getReason(), l.getStatus(),
                l.getApprovedBy() != null ? l.getApprovedBy() : "-"
            });
        }
    }

    private void searchLeave() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            loadLeaveData();
            return;
        }
        tableModel.setRowCount(0);
        List<Leave> list = leaveDAO.searchLeaves(kw);
        for (Leave l : list) {
            tableModel.addRow(new Object[]{
                l.getLeaveId(), l.getStudentName(), l.getLeaveFrom(),
                l.getLeaveTo(), l.getReason(), l.getStatus(),
                l.getApprovedBy() != null ? l.getApprovedBy() : "-"
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtLeaveId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtLeaveFrom.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtLeaveTo.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtReason.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        cbStatus.setSelectedItem(tableModel.getValueAt(row, 5));
        txtApprovedBy.setText(String.valueOf(tableModel.getValueAt(row, 6)).equals("-") ? (currentUser != null ? currentUser.getUsername() : "admin") : String.valueOf(tableModel.getValueAt(row, 6)));
    }

    private void addLeave() {
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String reason = txtReason.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Leave reason is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Leave l = new Leave();
            l.setStudentId(s.getStudentId());
            l.setLeaveFrom(Date.valueOf(txtLeaveFrom.getText().trim()));
            l.setLeaveTo(Date.valueOf(txtLeaveTo.getText().trim()));
            l.setReason(reason);
            l.setStatus((String) cbStatus.getSelectedItem());
            l.setApprovedBy(currentUser != null ? currentUser.getUsername() : "admin");

            if (leaveDAO.addLeave(l)) {
                JOptionPane.showMessageDialog(this, "Leave application submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLeaveData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit leave application.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String status) {
        if (txtLeaveId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a leave request to " + status.toLowerCase() + ".", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int leaveId = Integer.parseInt(txtLeaveId.getText());
        String adminName = currentUser != null ? currentUser.getUsername() : "admin";

        if (leaveDAO.updateLeaveStatus(leaveId, status, adminName)) {
            JOptionPane.showMessageDialog(this, "Leave request " + status.toLowerCase() + " successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadLeaveData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update leave status.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLeave() {
        if (txtLeaveId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a leave record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this leave record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtLeaveId.getText());
            if (leaveDAO.deleteLeave(id)) {
                JOptionPane.showMessageDialog(this, "Leave record deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLeaveData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete leave record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtLeaveId.setText("");
        txtLeaveFrom.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtLeaveTo.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtReason.setText("");
        cbStatus.setSelectedIndex(0);
        txtApprovedBy.setText(currentUser != null ? currentUser.getUsername() : "admin");
        leaveTable.clearSelection();
    }
}

