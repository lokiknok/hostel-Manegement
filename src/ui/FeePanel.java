package ui;

import dao.FeeDAO;
import dao.StudentDAO;
import model.Fee;
import model.Student;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FeePanel extends JPanel {
    private FeeDAO feeDAO;
    private StudentDAO studentDAO;

    private JTextField txtFeeId, txtRoomNo, txtFeeAmount, txtPaidAmount, txtPendingAmount, txtPaymentDate, txtRemarks, txtSearch;
    private JComboBox<Student> cbStudents;
    private JComboBox<String> cbPaymentMethod, cbPaymentStatus;
    private JTable feeTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;

    public FeePanel() {
        feeDAO = new FeeDAO();
        studentDAO = new StudentDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadStudentsDropdown();
        loadFeeData();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Fee Management & Payments");
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

        txtFeeId = new JTextField(); txtFeeId.setEditable(false);
        cbStudents = new JComboBox<>();
        txtRoomNo = new JTextField(); txtRoomNo.setEditable(false);
        txtFeeAmount = new JTextField("50000.00");
        txtPaidAmount = new JTextField("0.00");
        txtPendingAmount = new JTextField("50000.00"); txtPendingAmount.setEditable(false);
        txtPaymentDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        cbPaymentMethod = new JComboBox<>(new String[]{"Cash", "UPI", "Card", "Bank Transfer"});
        cbPaymentStatus = new JComboBox<>(new String[]{"PAID", "PARTIAL", "PENDING"}); cbPaymentStatus.setEnabled(false);
        txtRemarks = new JTextField();

        cbStudents.addActionListener(e -> {
            Student s = (Student) cbStudents.getSelectedItem();
            if (s != null) {
                txtRoomNo.setText(s.getRoomNumber() != null ? s.getRoomNumber() : "N/A");
            }
        });

        // Recalculate pending amount dynamically
        txtFeeAmount.addActionListener(e -> recalculatePending());
        txtPaidAmount.addActionListener(e -> recalculatePending());

        addFormField(formPanel, gbc, 0, "Payment ID:", txtFeeId);
        addFormField(formPanel, gbc, 1, "Select Student*:", cbStudents);
        addFormField(formPanel, gbc, 2, "Room Number:", txtRoomNo);
        addFormField(formPanel, gbc, 3, "Fee Amount*:", txtFeeAmount);
        addFormField(formPanel, gbc, 4, "Paid Amount*:", txtPaidAmount);
        addFormField(formPanel, gbc, 5, "Pending Amount:", txtPendingAmount);
        addFormField(formPanel, gbc, 6, "Payment Date:", txtPaymentDate);
        addFormField(formPanel, gbc, 7, "Payment Method:", cbPaymentMethod);
        addFormField(formPanel, gbc, 8, "Payment Status:", cbPaymentStatus);
        addFormField(formPanel, gbc, 9, "Remarks:", txtRemarks);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = UIUtils.createStyledButton("Add Payment", UIUtils.SUCCESS_GREEN, Color.WHITE);
        btnUpdate = UIUtils.createStyledButton("Update", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnDelete = UIUtils.createStyledButton("Delete", UIUtils.DANGER_RED, Color.WHITE);
        btnClear = UIUtils.createStyledButton("Clear", UIUtils.TEXT_MUTED, Color.WHITE);
        btnRefresh = UIUtils.createStyledButton("Refresh", UIUtils.NAVY_DARK, Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
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

        searchPanel.add(new JLabel("Search Payments:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // JTable
        String[] cols = {"ID", "Student Name", "Room", "Fee Amount", "Paid Amount", "Pending", "Date", "Method", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        feeTable = new JTable(tableModel);
        UIUtils.styleTable(feeTable);

        JScrollPane tableScroll = new JScrollPane(feeTable);
        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> addPayment());
        btnUpdate.addActionListener(e -> updatePayment());
        btnDelete.addActionListener(e -> deletePayment());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadStudentsDropdown();
            loadFeeData();
        });
        btnSearch.addActionListener(e -> searchPayment());

        feeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && feeTable.getSelectedRow() != -1) {
                populateFormFromTable(feeTable.getSelectedRow());
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

    private void recalculatePending() {
        try {
            double fee = Double.parseDouble(txtFeeAmount.getText().trim());
            double paid = Double.parseDouble(txtPaidAmount.getText().trim());
            double pending = Math.max(0, fee - paid);
            txtPendingAmount.setText(String.format("%.2f", pending));

            if (paid >= fee) cbPaymentStatus.setSelectedItem("PAID");
            else if (paid > 0) cbPaymentStatus.setSelectedItem("PARTIAL");
            else cbPaymentStatus.setSelectedItem("PENDING");
        } catch (NumberFormatException ignored) {}
    }

    public void loadStudentsDropdown() {
        cbStudents.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cbStudents.addItem(s);
        }
    }

    public void loadFeeData() {
        tableModel.setRowCount(0);
        List<Fee> list = feeDAO.getAllFees();
        for (Fee f : list) {
            tableModel.addRow(new Object[]{
                f.getFeeId(), f.getStudentName(), f.getRoomNumber() != null ? f.getRoomNumber() : "N/A",
                String.format("%.2f", f.getFeeAmount()), String.format("%.2f", f.getPaidAmount()),
                String.format("%.2f", f.getPendingAmount()), f.getPaymentDate(), f.getPaymentMethod(), f.getPaymentStatus()
            });
        }
    }

    private void searchPayment() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            loadFeeData();
            return;
        }
        tableModel.setRowCount(0);
        List<Fee> list = feeDAO.searchFees(kw);
        for (Fee f : list) {
            tableModel.addRow(new Object[]{
                f.getFeeId(), f.getStudentName(), f.getRoomNumber() != null ? f.getRoomNumber() : "N/A",
                String.format("%.2f", f.getFeeAmount()), String.format("%.2f", f.getPaidAmount()),
                String.format("%.2f", f.getPendingAmount()), f.getPaymentDate(), f.getPaymentMethod(), f.getPaymentStatus()
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtFeeId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtRoomNo.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtFeeAmount.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtPaidAmount.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtPendingAmount.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtPaymentDate.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        cbPaymentMethod.setSelectedItem(tableModel.getValueAt(row, 7));
        cbPaymentStatus.setSelectedItem(tableModel.getValueAt(row, 8));
    }

    private boolean validateInputs() {
        if (cbStudents.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select a student.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            double fee = Double.parseDouble(txtFeeAmount.getText().trim());
            double paid = Double.parseDouble(txtPaidAmount.getText().trim());
            if (fee < 0 || paid < 0) {
                JOptionPane.showMessageDialog(this, "Fee amount and Paid amount cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (paid > fee) {
                JOptionPane.showMessageDialog(this, "Paid amount cannot exceed total fee amount.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Fee & Paid Amounts.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addPayment() {
        recalculatePending();
        if (!validateInputs()) return;

        Student student = (Student) cbStudents.getSelectedItem();
        Fee f = new Fee();
        f.setStudentId(student.getStudentId());
        f.setFeeAmount(Double.parseDouble(txtFeeAmount.getText().trim()));
        f.setPaidAmount(Double.parseDouble(txtPaidAmount.getText().trim()));
        f.setPaymentDate(Date.valueOf(txtPaymentDate.getText().trim()));
        f.setPaymentMethod((String) cbPaymentMethod.getSelectedItem());
        f.setRemarks(txtRemarks.getText().trim());

        if (feeDAO.addFee(f)) {
            JOptionPane.showMessageDialog(this, "Payment recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadFeeData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to record payment.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePayment() {
        if (txtFeeId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a payment record to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        recalculatePending();
        if (!validateInputs()) return;

        Student student = (Student) cbStudents.getSelectedItem();
        Fee f = new Fee();
        f.setFeeId(Integer.parseInt(txtFeeId.getText()));
        f.setStudentId(student.getStudentId());
        f.setFeeAmount(Double.parseDouble(txtFeeAmount.getText().trim()));
        f.setPaidAmount(Double.parseDouble(txtPaidAmount.getText().trim()));
        f.setPaymentDate(Date.valueOf(txtPaymentDate.getText().trim()));
        f.setPaymentMethod((String) cbPaymentMethod.getSelectedItem());
        f.setRemarks(txtRemarks.getText().trim());

        if (feeDAO.updateFee(f)) {
            JOptionPane.showMessageDialog(this, "Payment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadFeeData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update payment.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePayment() {
        if (txtFeeId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a payment record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this fee record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int feeId = Integer.parseInt(txtFeeId.getText());
            if (feeDAO.deleteFee(feeId)) {
                JOptionPane.showMessageDialog(this, "Fee record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFeeData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete fee record.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtFeeId.setText("");
        txtFeeAmount.setText("50000.00");
        txtPaidAmount.setText("0.00");
        txtPendingAmount.setText("50000.00");
        txtPaymentDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        cbPaymentMethod.setSelectedIndex(0);
        cbPaymentStatus.setSelectedItem("PENDING");
        txtRemarks.setText("");
        feeTable.clearSelection();
    }
}

