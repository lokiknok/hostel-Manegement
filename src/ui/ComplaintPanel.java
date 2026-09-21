package ui;

import dao.ComplaintDAO;
import dao.StudentDAO;
import model.Complaint;
import model.Student;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ComplaintPanel extends JPanel {
    private ComplaintDAO complaintDAO;
    private StudentDAO studentDAO;

    private JTextField txtComplaintId, txtComplaintDate, txtSearch;
    private JTextArea txtDescription, txtResolution;
    private JComboBox<Student> cbStudents;
    private JComboBox<String> cbType, cbPriority, cbStatus;
    private JTable complaintTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnUpdateStatus, btnDelete, btnClear, btnSearch, btnRefresh;

    public ComplaintPanel() {
        complaintDAO = new ComplaintDAO();
        studentDAO = new StudentDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadStudentsDropdown();
        loadComplaintData();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Complaint Management");
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

        txtComplaintId = new JTextField(); txtComplaintId.setEditable(false);
        cbStudents = new JComboBox<>();
        cbType = new JComboBox<>(new String[]{"Electrical", "Plumbing", "Food", "Room", "Cleaning", "Internet", "Other"});
        cbPriority = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        txtComplaintDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        
        txtDescription = new JTextArea(3, 15);
        txtDescription.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);

        cbStatus = new JComboBox<>(new String[]{"PENDING", "IN PROGRESS", "RESOLVED"});

        txtResolution = new JTextArea(3, 15);
        txtResolution.setLineWrap(true);
        JScrollPane resScroll = new JScrollPane(txtResolution);

        addFormField(formPanel, gbc, 0, "Complaint ID:", txtComplaintId);
        addFormField(formPanel, gbc, 1, "Select Student*:", cbStudents);
        addFormField(formPanel, gbc, 2, "Complaint Type:", cbType);
        addFormField(formPanel, gbc, 3, "Priority:", cbPriority);
        addFormField(formPanel, gbc, 4, "Date (YYYY-MM-DD):", txtComplaintDate);
        addFormField(formPanel, gbc, 5, "Description*:", descScroll);
        addFormField(formPanel, gbc, 6, "Status:", cbStatus);
        addFormField(formPanel, gbc, 7, "Resolution Notes:", resScroll);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = UIUtils.createStyledButton("Log Complaint", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnUpdateStatus = UIUtils.createStyledButton("Update Status", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Delete", UIUtils.DANGER_RED, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Clear", UIUtils.TEXT_MUTED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdateStatus);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setPreferredSize(new Dimension(380, 0));
        formScroll.setBorder(null);

        // Right Panel (Search + Table)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        txtSearch = new JTextField(20);
        btnSearch = UIUtils.createStyledButton("Search", UIUtils.ACCENT_BLUE, Color.WHITE);

        searchPanel.add(new JLabel("Search Complaints:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // JTable
        String[] cols = {"ID", "Student Name", "Type", "Priority", "Status", "Date", "Description", "Resolution"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        complaintTable = new JTable(tableModel);
        UIUtils.styleTable(complaintTable);

        JScrollPane tableScroll = new JScrollPane(complaintTable);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Handlers
        btnAdd.addActionListener(e -> addComplaint());
        btnUpdateStatus.addActionListener(e -> updateComplaintStatus());
        btnDelete.addActionListener(e -> deleteComplaint());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadStudentsDropdown();
            loadComplaintData();
        });
        btnSearch.addActionListener(e -> searchComplaint());

        complaintTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && complaintTable.getSelectedRow() != -1) {
                populateFormFromTable(complaintTable.getSelectedRow());
            }
        });
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_SMALL);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    public void loadStudentsDropdown() {
        cbStudents.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudents.addItem(s);
        }
    }

    public void loadComplaintData() {
        tableModel.setRowCount(0);
        List<Complaint> list = complaintDAO.getAllComplaints();
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                c.getComplaintId(), c.getStudentName(), c.getComplaintType(),
                c.getPriority(), c.getStatus(), c.getComplaintDate(),
                c.getDescription(), c.getResolution() != null ? c.getResolution() : "-"
            });
        }
    }

    private void searchComplaint() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            loadComplaintData();
            return;
        }
        tableModel.setRowCount(0);
        List<Complaint> list = complaintDAO.searchComplaints(kw);
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                c.getComplaintId(), c.getStudentName(), c.getComplaintType(),
                c.getPriority(), c.getStatus(), c.getComplaintDate(),
                c.getDescription(), c.getResolution() != null ? c.getResolution() : "-"
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtComplaintId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        cbType.setSelectedItem(tableModel.getValueAt(row, 2));
        cbPriority.setSelectedItem(tableModel.getValueAt(row, 3));
        cbStatus.setSelectedItem(tableModel.getValueAt(row, 4));
        txtComplaintDate.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtDescription.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        txtResolution.setText(String.valueOf(tableModel.getValueAt(row, 7)).equals("-") ? "" : String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void addComplaint() {
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String desc = txtDescription.getText().trim();
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Complaint c = new Complaint();
            c.setStudentId(s.getStudentId());
            c.setComplaintType((String) cbType.getSelectedItem());
            c.setPriority((String) cbPriority.getSelectedItem());
            c.setComplaintDate(Date.valueOf(txtComplaintDate.getText().trim()));
            c.setDescription(desc);
            c.setStatus((String) cbStatus.getSelectedItem());
            c.setResolution(txtResolution.getText().trim());

            if (complaintDAO.addComplaint(c)) {
                JOptionPane.showMessageDialog(this, "Complaint logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComplaintData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to log complaint.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateComplaintStatus() {
        if (txtComplaintId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a complaint from table to update status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int complaintId = Integer.parseInt(txtComplaintId.getText());
        String status = (String) cbStatus.getSelectedItem();
        String resolution = txtResolution.getText().trim();

        if (complaintDAO.updateComplaintStatus(complaintId, status, resolution)) {
            JOptionPane.showMessageDialog(this, "Complaint status & resolution updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadComplaintData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update complaint status.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteComplaint() {
        if (txtComplaintId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a complaint to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this complaint record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtComplaintId.getText());
            if (complaintDAO.deleteComplaint(id)) {
                JOptionPane.showMessageDialog(this, "Complaint deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComplaintData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete complaint.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtComplaintId.setText("");
        cbType.setSelectedIndex(0);
        cbPriority.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtComplaintDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtDescription.setText("");
        txtResolution.setText("");
        complaintTable.clearSelection();
    }
}

