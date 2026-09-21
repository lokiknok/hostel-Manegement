package ui;

import dao.StudentDAO;
import dao.VisitorDAO;
import model.Student;
import model.Visitor;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class VisitorPanel extends JPanel {
    private VisitorDAO visitorDAO;
    private StudentDAO studentDAO;

    private JTextField txtVisitorId, txtVisitorName, txtRelationship, txtPhone, txtVisitDate, txtEntryTime, txtExitTime, txtPurpose, txtSearch;
    private JComboBox<Student> cbStudents;
    private JTable visitorTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;

    public VisitorPanel() {
        visitorDAO = new VisitorDAO();
        studentDAO = new StudentDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadStudentsDropdown();
        loadVisitorData();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Visitor Management");
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

        txtVisitorId = new JTextField(); txtVisitorId.setEditable(false);
        cbStudents = new JComboBox<>();
        txtVisitorName = new JTextField();
        txtRelationship = new JTextField();
        txtPhone = new JTextField();
        txtVisitDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtEntryTime = new JTextField("10:00:00");
        txtExitTime = new JTextField("12:00:00");
        txtPurpose = new JTextField();

        addFormField(formPanel, gbc, 0, "Visitor ID:", txtVisitorId);
        addFormField(formPanel, gbc, 1, "Select Student*:", cbStudents);
        addFormField(formPanel, gbc, 2, "Visitor Name*:", txtVisitorName);
        addFormField(formPanel, gbc, 3, "Relationship*:", txtRelationship);
        addFormField(formPanel, gbc, 4, "Visitor Phone*:", txtPhone);
        addFormField(formPanel, gbc, 5, "Visit Date (YYYY-MM-DD)*:", txtVisitDate);
        addFormField(formPanel, gbc, 6, "Entry Time (HH:MM:SS)*:", txtEntryTime);
        addFormField(formPanel, gbc, 7, "Exit Time (HH:MM:SS):", txtExitTime);
        addFormField(formPanel, gbc, 8, "Purpose of Visit:", txtPurpose);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = UIUtils.createStyledButton("Add Visitor", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnUpdate = UIUtils.createStyledButton("Update", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Delete", UIUtils.DANGER_RED, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Clear", UIUtils.TEXT_MUTED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
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

        searchPanel.add(new JLabel("Search Visitors:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // JTable
        String[] cols = {"ID", "Student Name", "Visitor Name", "Relationship", "Phone", "Date", "Entry", "Exit", "Purpose"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        visitorTable = new JTable(tableModel);
        UIUtils.styleTable(visitorTable);

        JScrollPane tableScroll = new JScrollPane(visitorTable);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Handlers
        btnAdd.addActionListener(e -> addVisitor());
        btnUpdate.addActionListener(e -> updateVisitor());
        btnDelete.addActionListener(e -> deleteVisitor());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadStudentsDropdown();
            loadVisitorData();
        });
        btnSearch.addActionListener(e -> searchVisitor());

        visitorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && visitorTable.getSelectedRow() != -1) {
                populateFormFromTable(visitorTable.getSelectedRow());
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

    public void loadVisitorData() {
        tableModel.setRowCount(0);
        List<Visitor> list = visitorDAO.getAllVisitors();
        for (Visitor v : list) {
            tableModel.addRow(new Object[]{
                v.getVisitorId(), v.getStudentName(), v.getVisitorName(),
                v.getRelationship(), v.getPhone(), v.getVisitDate(),
                v.getEntryTime(), v.getExitTime() != null ? v.getExitTime() : "-", v.getPurpose()
            });
        }
    }

    private void searchVisitor() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            loadVisitorData();
            return;
        }
        tableModel.setRowCount(0);
        List<Visitor> list = visitorDAO.searchVisitors(kw);
        for (Visitor v : list) {
            tableModel.addRow(new Object[]{
                v.getVisitorId(), v.getStudentName(), v.getVisitorName(),
                v.getRelationship(), v.getPhone(), v.getVisitDate(),
                v.getEntryTime(), v.getExitTime() != null ? v.getExitTime() : "-", v.getPurpose()
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtVisitorId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtVisitorName.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtRelationship.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtPhone.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtVisitDate.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtEntryTime.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        txtExitTime.setText(String.valueOf(tableModel.getValueAt(row, 7)).equals("-") ? "" : String.valueOf(tableModel.getValueAt(row, 7)));
        txtPurpose.setText(String.valueOf(tableModel.getValueAt(row, 8)));
    }

    private boolean validateInputs() {
        if (cbStudents.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtVisitorName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Visitor Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtRelationship.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Relationship is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty() || !phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Valid phone number (10-15 digits) is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addVisitor() {
        if (!validateInputs()) return;
        Student s = (Student) cbStudents.getSelectedItem();

        try {
            Visitor v = new Visitor();
            v.setStudentId(s.getStudentId());
            v.setVisitorName(txtVisitorName.getText().trim());
            v.setRelationship(txtRelationship.getText().trim());
            v.setPhone(txtPhone.getText().trim());
            v.setVisitDate(Date.valueOf(txtVisitDate.getText().trim()));
            v.setEntryTime(Time.valueOf(txtEntryTime.getText().trim()));
            String exitStr = txtExitTime.getText().trim();
            if (!exitStr.isEmpty()) v.setExitTime(Time.valueOf(exitStr));
            v.setPurpose(txtPurpose.getText().trim());

            if (visitorDAO.addVisitor(v)) {
                JOptionPane.showMessageDialog(this, "Visitor record added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadVisitorData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add visitor.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date/Time format! Date: YYYY-MM-DD, Time: HH:MM:SS.", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVisitor() {
        if (txtVisitorId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a visitor record to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;
        Student s = (Student) cbStudents.getSelectedItem();

        try {
            Visitor v = new Visitor();
            v.setVisitorId(Integer.parseInt(txtVisitorId.getText()));
            v.setStudentId(s.getStudentId());
            v.setVisitorName(txtVisitorName.getText().trim());
            v.setRelationship(txtRelationship.getText().trim());
            v.setPhone(txtPhone.getText().trim());
            v.setVisitDate(Date.valueOf(txtVisitDate.getText().trim()));
            v.setEntryTime(Time.valueOf(txtEntryTime.getText().trim()));
            String exitStr = txtExitTime.getText().trim();
            if (!exitStr.isEmpty() && !exitStr.equals("-")) v.setExitTime(Time.valueOf(exitStr));
            v.setPurpose(txtPurpose.getText().trim());

            if (visitorDAO.updateVisitor(v)) {
                JOptionPane.showMessageDialog(this, "Visitor record updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadVisitorData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update visitor record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date/Time format!", "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteVisitor() {
        if (txtVisitorId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a visitor record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this visitor record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtVisitorId.getText());
            if (visitorDAO.deleteVisitor(id)) {
                JOptionPane.showMessageDialog(this, "Visitor record deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadVisitorData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtVisitorId.setText("");
        txtVisitorName.setText("");
        txtRelationship.setText("");
        txtPhone.setText("");
        txtVisitDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtEntryTime.setText("10:00:00");
        txtExitTime.setText("12:00:00");
        txtPurpose.setText("");
        visitorTable.clearSelection();
    }
}

