package ui;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class UIUtils {
    // Professional Color Palette
    public static final Color NAVY_DARK = new Color(30, 41, 59);     // #1e293b
    public static final Color NAVY_SIDEBAR = new Color(15, 23, 42);  // #0f172a
    public static final Color ACCENT_BLUE = new Color(37, 99, 235);   // #2563eb
    public static final Color ACCENT_HOVER = new Color(29, 78, 216);  // #1d4ed8
    public static final Color BG_LIGHT = new Color(248, 250, 252);    // #f8fafc
    public static final Color CARD_BG = new Color(255, 255, 255);
    public static final Color TEXT_DARK = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);
    public static final Color WARNING_AMBER = new Color(245, 158, 11);
    public static final Color DANGER_RED = new Color(239, 68, 68);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return btn;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(28);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(NAVY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 32));

        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    public static JPanel createCardPanel(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
            )
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    public static boolean exportTableToCSV(JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as CSV");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int colCount = model.getColumnCount();
                int rowCount = model.getRowCount();

                // Header
                for (int i = 0; i < colCount; i++) {
                    writer.write("\"" + model.getColumnName(i) + "\"");
                    if (i < colCount - 1) writer.write(",");
                }
                writer.write("\n");

                // Rows
                for (int r = 0; r < rowCount; r++) {
                    for (int c = 0; c < colCount; c++) {
                        Object val = model.getValueAt(r, c);
                        String text = (val != null) ? val.toString().replace("\"", "\"\"") : "";
                        writer.write("\"" + text + "\"");
                        if (c < colCount - 1) writer.write(",");
                    }
                    writer.write("\n");
                }
                writer.flush();
                JOptionPane.showMessageDialog(null, "Report exported successfully to:\n" + fileToSave.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error exporting CSV file:\n" + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return false;
    }
}

