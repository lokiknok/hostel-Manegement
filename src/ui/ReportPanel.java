package ui;

import dao.ReportDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ReportPanel extends JPanel {
    private ReportDAO reportDAO;
    private JComboBox<String> cbReportType;
    private JTable reportTable;
    private JButton btnLoad, btnExportCSV;

    public ReportPanel() {
        reportDAO = new ReportDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(UIUtils.BG_LIGHT);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadSelectedReport();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblHeader = new JLabel("System Reports & CSV Export");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.NAVY_DARK);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        cbReportType = new JComboBox<>(new String[]{
            "1. Student List",
            "2. Room Occupancy Report",
            "3. Fee Report",
            "4. Pending Fee Report",
            "5. Attendance Report",
            "6. Complaint Report",
            "7. Visitor Report",
            "8. Leave Report"
        });
        cbReportType.setFont(UIUtils.FONT_BOLD);

        btnLoad = UIUtils.createStyledButton("Generate Report", UIUtils.ACCENT_BLUE, Color.WHITE);
        btnExportCSV = UIUtils.createStyledButton("Export to CSV", UIUtils.SUCCESS_GREEN, Color.WHITE);

        controlPanel.add(new JLabel("Select Report Type:"));
        controlPanel.add(cbReportType);
        controlPanel.add(btnLoad);
        controlPanel.add(btnExportCSV);

        // Table
        reportTable = new JTable();
        UIUtils.styleTable(reportTable);
        JScrollPane tableScroll = new JScrollPane(reportTable);

        add(headerPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.PAGE_START);

        // Wrapper for header + control panel
        JPanel topWrapper = new JPanel(new BorderLayout(0, 10));
        topWrapper.setOpaque(false);
        topWrapper.add(headerPanel, BorderLayout.NORTH);
        topWrapper.add(controlPanel, BorderLayout.SOUTH);

        add(topWrapper, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Handlers
        btnLoad.addActionListener(e -> loadSelectedReport());
        cbReportType.addActionListener(e -> loadSelectedReport());
        btnExportCSV.addActionListener(e -> {
            String selectedReport = (String) cbReportType.getSelectedItem();
            String defaultFileName = selectedReport.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase() + ".csv";
            UIUtils.exportTableToCSV(reportTable, defaultFileName);
        });
    }

    public void loadSelectedReport() {
        int index = cbReportType.getSelectedIndex();
        DefaultTableModel model;
        switch (index) {
            case 0:
                model = reportDAO.getStudentListReport();
                break;
            case 1:
                model = reportDAO.getRoomOccupancyReport();
                break;
            case 2:
                model = reportDAO.getFeeReport();
                break;
            case 3:
                model = reportDAO.getPendingFeeReport();
                break;
            case 4:
                model = reportDAO.getAttendanceReport();
                break;
            case 5:
                model = reportDAO.getComplaintReport();
                break;
            case 6:
                model = reportDAO.getVisitorReport();
                break;
            case 7:
                model = reportDAO.getLeaveReport();
                break;
            default:
                model = new DefaultTableModel();
                break;
        }
        reportTable.setModel(model);
        UIUtils.styleTable(reportTable);
    }
}

