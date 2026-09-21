package ui;

import dao.StudentDAO;
import model.Student;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class StudentPanel extends JPanel {
    private StudentDAO studentDAO;

    private JTextField txtStudentId, txtCode, txtName, txtDob, txtPhone, txtEmail, txtDept, txtCourse, txtAddress, txtParentName, txtParentPhone, txtRoomNo, txtAdmissionDate, txtSearch;
    private JComboBox<String> cbGender, cbYear, cbStatus;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;

    public StudentPanel() {
        studentDAO = new StudentDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadStudentData();
    }

    private void initUI() {
        // Title Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Student Management");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.NAVY_DARK);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        // Form Panel (Left)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(); txtStudentId.setEditable(false);
        txtCode = new JTextField();
        txtName = new JTextField();
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        txtDob = new JTextField("2002-01-01");
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtDept = new JTextField();
        txtCourse = new JTextField();
        cbYear = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        txtAddress = new JTextField();
        txtParentName = new JTextField();
        txtParentPhone = new JTextField();
        txtRoomNo = new JTextField(); txtRoomNo.setEditable(false);
        txtAdmissionDate = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        addFormField(formPanel, gbc, 0, "ID:", txtStudentId);
        addFormField(formPanel, gbc, 1, "Student Code*:", txtCode);
        addFormField(formPanel, gbc, 2, "Full Name*:", txtName);
        addFormField(formPanel, gbc, 3, "Gender:", cbGender);
        addFormField(formPanel, gbc, 4, "Date of Birth (YYYY-MM-DD):", txtDob);
        addFormField(formPanel, gbc, 5, "Phone*:", txtPhone);
        addFormField(formPanel, gbc, 6, "Email*:", txtEmail);
        addFormField(formPanel, gbc, 7, "Department:", txtDept);
        addFormField(formPanel, gbc, 8, "Course:", txtCourse);
        addFormField(formPanel, gbc, 9, "Year:", cbYear);
        addFormField(formPanel, gbc, 10, "Address:", txtAddress);
        addFormField(formPanel, gbc, 11, "Parent Name:", txtParentName);
        addFormField(formPanel, gbc, 12, "Parent Phone:", txtParentPhone);
        addFormField(formPanel, gbc, 13, "Room Number:", txtRoomNo);
        addFormField(formPanel, gbc, 14, "Admission Date:", txtAdmissionDate);
        addFormField(formPanel, gbc, 15, "Status:", cbStatus);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = UIUtils.createStyledButton("Add", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnUpdate = UIUtils.createStyledButton("Update", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Delete", UIUtils.DANGER_RED, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Clear", UIUtils.TEXT_MUTED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 16; gbc.gridwidth = 2;
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

        searchPanel.add(new JLabel("Search Student:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // JTable
        String[] cols = {"ID", "Code", "Name", "Gender", "Phone", "Email", "Department", "Course", "Year", "Room", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(tableModel);
        UIUtils.styleTable(studentTable);

        JScrollPane tableScroll = new JScrollPane(studentTable);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Event Listeners
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadStudentData());
        btnSearch.addActionListener(e -> searchStudent());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                populateFormFromTable(studentTable.getSelectedRow());
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

    public void loadStudentData() {
        tableModel.setRowCount(0);
        List<Student> list = studentDAO.getAllStudents();
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                s.getStudentId(), s.getStudentCode(), s.getName(), s.getGender(),
                s.getPhone(), s.getEmail(), s.getDepartment(), s.getCourse(),
                s.getYearOfStudy(), s.getRoomNumber() != null ? s.getRoomNumber() : "Unassigned", s.getStatus()
            });
        }
    }

    private void searchStudent() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadStudentData();
            return;
        }
        tableModel.setRowCount(0);
        List<Student> list = studentDAO.searchStudents(keyword);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                s.getStudentId(), s.getStudentCode(), s.getName(), s.getGender(),
                s.getPhone(), s.getEmail(), s.getDepartment(), s.getCourse(),
                s.getYearOfStudy(), s.getRoomNumber() != null ? s.getRoomNumber() : "Unassigned", s.getStatus()
            });
        }
    }

    private void populateFormFromTable(int row) {
        int studentId = (int) tableModel.getValueAt(row, 0);
        Student s = studentDAO.getStudentById(studentId);
        if (s != null) {
            txtStudentId.setText(String.valueOf(s.getStudentId()));
            txtCode.setText(s.getStudentCode());
            txtName.setText(s.getName());
            cbGender.setSelectedItem(s.getGender());
            txtDob.setText(s.getDob() != null ? s.getDob().toString() : "");
            txtPhone.setText(s.getPhone());
            txtEmail.setText(s.getEmail());
            txtDept.setText(s.getDepartment());
            txtCourse.setText(s.getCourse());
            cbYear.setSelectedItem(s.getYearOfStudy());
            txtAddress.setText(s.getAddress());
            txtParentName.setText(s.getParentName());
            txtParentPhone.setText(s.getParentPhone());
            txtRoomNo.setText(s.getRoomNumber() != null ? s.getRoomNumber() : "Unassigned");
            txtAdmissionDate.setText(s.getAdmissionDate() != null ? s.getAdmissionDate().toString() : "");
            cbStatus.setSelectedItem(s.getStatus());
        }
    }

    private boolean validateInputs() {
        if (txtCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student Code is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty() || !phone.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Valid phone number (10-15 digits) is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String email = txtEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Valid email address is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addStudent() {
        if (!validateInputs()) return;
        try {
            Student s = new Student();
            s.setStudentCode(txtCode.getText().trim());
            s.setName(txtName.getText().trim());
            s.setGender((String) cbGender.getSelectedItem());
            s.setDob(Date.valueOf(txtDob.getText().trim()));
            s.setPhone(txtPhone.getText().trim());
            s.setEmail(txtEmail.getText().trim());
            s.setDepartment(txtDept.getText().trim());
            s.setCourse(txtCourse.getText().trim());
            s.setYearOfStudy((String) cbYear.getSelectedItem());
            s.setAddress(txtAddress.getText().trim());
            s.setParentName(txtParentName.getText().trim());
            s.setParentPhone(txtParentPhone.getText().trim());
            s.setRoomNumber(txtRoomNo.getText().trim().equals("Unassigned") ? null : txtRoomNo.getText().trim());
            s.setAdmissionDate(Date.valueOf(txtAdmissionDate.getText().trim()));
            s.setStatus((String) cbStatus.getSelectedItem());

            if (studentDAO.addStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student. Student code may already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (txtStudentId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student from table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        try {
            Student s = new Student();
            s.setStudentId(Integer.parseInt(txtStudentId.getText()));
            s.setStudentCode(txtCode.getText().trim());
            s.setName(txtName.getText().trim());
            s.setGender((String) cbGender.getSelectedItem());
            s.setDob(Date.valueOf(txtDob.getText().trim()));
            s.setPhone(txtPhone.getText().trim());
            s.setEmail(txtEmail.getText().trim());
            s.setDepartment(txtDept.getText().trim());
            s.setCourse(txtCourse.getText().trim());
            s.setYearOfStudy((String) cbYear.getSelectedItem());
            s.setAddress(txtAddress.getText().trim());
            s.setParentName(txtParentName.getText().trim());
            s.setParentPhone(txtParentPhone.getText().trim());
            s.setRoomNumber(txtRoomNo.getText().trim().equals("Unassigned") ? null : txtRoomNo.getText().trim());
            s.setAdmissionDate(Date.valueOf(txtAdmissionDate.getText().trim()));
            s.setStatus((String) cbStatus.getSelectedItem());

            if (studentDAO.updateStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update student.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        if (txtStudentId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student from table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int studentId = Integer.parseInt(txtStudentId.getText());
            if (studentDAO.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtStudentId.setText("");
        txtCode.setText("");
        txtName.setText("");
        cbGender.setSelectedIndex(0);
        txtDob.setText("2002-01-01");
        txtPhone.setText("");
        txtEmail.setText("");
        txtDept.setText("");
        txtCourse.setText("");
        cbYear.setSelectedIndex(0);
        txtAddress.setText("");
        txtParentName.setText("");
        txtParentPhone.setText("");
        txtRoomNo.setText("Unassigned");
        txtAdmissionDate.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        cbStatus.setSelectedIndex(0);
        studentTable.clearSelection();
    }
}

