package ui;

import dao.RoomDAO;
import model.Room;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class RoomPanel extends JPanel {
    private RoomDAO roomDAO;

    private JTextField txtRoomId, txtRoomNumber, txtFloor, txtCapacity, txtOccupiedBeds, txtAvailableBeds, txtStatus;
    private JComboBox<String> cbRoomType;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;

    public RoomPanel() {
        roomDAO = new RoomDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadRoomData();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("Room Management");
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

        txtRoomId = new JTextField(); txtRoomId.setEditable(false);
        txtRoomNumber = new JTextField();
        txtFloor = new JTextField("1");
        cbRoomType = new JComboBox<>(new String[]{"Single", "Double", "Triple", "Four Sharing"});
        txtCapacity = new JTextField("2");
        txtOccupiedBeds = new JTextField("0");
        txtAvailableBeds = new JTextField("2"); txtAvailableBeds.setEditable(false);
        txtStatus = new JTextField("AVAILABLE"); txtStatus.setEditable(false);

        // Auto capacity assignment on room type select
        cbRoomType.addActionListener(e -> {
            String type = (String) cbRoomType.getSelectedItem();
            if ("Single".equals(type)) txtCapacity.setText("1");
            else if ("Double".equals(type)) txtCapacity.setText("2");
            else if ("Triple".equals(type)) txtCapacity.setText("3");
            else if ("Four Sharing".equals(type)) txtCapacity.setText("4");
            recalculateBedsAndStatus();
        });

        addFormField(formPanel, gbc, 0, "Room ID:", txtRoomId);
        addFormField(formPanel, gbc, 1, "Room Number*:", txtRoomNumber);
        addFormField(formPanel, gbc, 2, "Floor*:", txtFloor);
        addFormField(formPanel, gbc, 3, "Room Type:", cbRoomType);
        addFormField(formPanel, gbc, 4, "Capacity*:", txtCapacity);
        addFormField(formPanel, gbc, 5, "Occupied Beds:", txtOccupiedBeds);
        addFormField(formPanel, gbc, 6, "Available Beds:", txtAvailableBeds);
        addFormField(formPanel, gbc, 7, "Room Status:", txtStatus);

        // Buttons
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

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setPreferredSize(new Dimension(360, 0));
        formScroll.setBorder(null);

        // Table Panel
        String[] cols = {"Room ID", "Room Number", "Floor", "Type", "Capacity", "Occupied", "Available", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomTable = new JTable(tableModel);
        UIUtils.styleTable(roomTable);

        JScrollPane tableScroll = new JScrollPane(roomTable);

        add(headerPanel, BorderLayout.NORTH);
        add(formScroll, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> addRoom());
        btnUpdate.addActionListener(e -> updateRoom());
        btnDelete.addActionListener(e -> deleteRoom());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadRoomData());

        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roomTable.getSelectedRow() != -1) {
                populateFormFromTable(roomTable.getSelectedRow());
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

    private void recalculateBedsAndStatus() {
        try {
            int cap = Integer.parseInt(txtCapacity.getText().trim());
            int occ = Integer.parseInt(txtOccupiedBeds.getText().trim());
            int avail = cap - occ;
            txtAvailableBeds.setText(String.valueOf(avail));
            txtStatus.setText(RoomDAO.calculateRoomStatus(cap, occ));
        } catch (Exception ignored) {}
    }

    public void loadRoomData() {
        tableModel.setRowCount(0);
        List<Room> list = roomDAO.getAllRooms();
        for (Room r : list) {
            tableModel.addRow(new Object[]{
                r.getRoomId(), r.getRoomNumber(), r.getFloor(), r.getRoomType(),
                r.getCapacity(), r.getOccupiedBeds(), r.getAvailableBeds(), r.getStatus()
            });
        }
    }

    private void populateFormFromTable(int row) {
        txtRoomId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtRoomNumber.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtFloor.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        cbRoomType.setSelectedItem(tableModel.getValueAt(row, 3));
        txtCapacity.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtOccupiedBeds.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtAvailableBeds.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        txtStatus.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private boolean validateInputs() {
        if (txtRoomNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            int cap = Integer.parseInt(txtCapacity.getText().trim());
            int occ = Integer.parseInt(txtOccupiedBeds.getText().trim());
            Integer.parseInt(txtFloor.getText().trim());
            if (cap <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (occ < 0 || occ > cap) {
                JOptionPane.showMessageDialog(this, "Occupied beds cannot be negative or exceed room capacity.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity, Floor, and Occupied Beds must be valid numbers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addRoom() {
        if (!validateInputs()) return;
        Room r = new Room();
        r.setRoomNumber(txtRoomNumber.getText().trim());
        r.setFloor(Integer.parseInt(txtFloor.getText().trim()));
        r.setRoomType((String) cbRoomType.getSelectedItem());
        r.setCapacity(Integer.parseInt(txtCapacity.getText().trim()));
        r.setOccupiedBeds(Integer.parseInt(txtOccupiedBeds.getText().trim()));

        if (roomDAO.addRoom(r)) {
            JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRoomData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add room. Room number may already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        if (txtRoomId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a room from table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        Room r = new Room();
        r.setRoomId(Integer.parseInt(txtRoomId.getText()));
        r.setRoomNumber(txtRoomNumber.getText().trim());
        r.setFloor(Integer.parseInt(txtFloor.getText().trim()));
        r.setRoomType((String) cbRoomType.getSelectedItem());
        r.setCapacity(Integer.parseInt(txtCapacity.getText().trim()));
        r.setOccupiedBeds(Integer.parseInt(txtOccupiedBeds.getText().trim()));

        if (roomDAO.updateRoom(r)) {
            JOptionPane.showMessageDialog(this, "Room updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRoomData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update room.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        if (txtRoomId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a room from table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this room?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int roomId = Integer.parseInt(txtRoomId.getText());
            if (roomDAO.deleteRoom(roomId)) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRoomData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room. It might be assigned to a student.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtRoomId.setText("");
        txtRoomNumber.setText("");
        txtFloor.setText("1");
        cbRoomType.setSelectedIndex(0);
        txtCapacity.setText("1");
        txtOccupiedBeds.setText("0");
        txtAvailableBeds.setText("1");
        txtStatus.setText("AVAILABLE");
        roomTable.clearSelection();
    }
}

