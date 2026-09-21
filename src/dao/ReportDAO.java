package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ReportDAO {

    public DefaultTableModel getStudentListReport() {
        String[] columnNames = {"ID", "Code", "Name", "Gender", "Phone", "Email", "Department", "Room No", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT student_id, student_code, name, gender, phone, email, department, IFNULL(room_number, 'N/A') as room_num, status FROM students ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("student_code"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getString("room_num"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getRoomOccupancyReport() {
        String[] columnNames = {"Room ID", "Room Number", "Floor", "Type", "Capacity", "Occupied", "Available", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT room_id, room_number, floor, room_type, capacity, occupied_beds, available_beds, status FROM rooms ORDER BY room_number ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getInt("floor"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getInt("occupied_beds"),
                    rs.getInt("available_beds"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getFeeReport() {
        String[] columnNames = {"Payment ID", "Student Name", "Room", "Fee Amount", "Paid Amount", "Pending Amount", "Date", "Method", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT f.fee_id, s.name as student_name, IFNULL(s.room_number, 'N/A') as room_num, f.fee_amount, f.paid_amount, f.pending_amount, f.payment_date, f.payment_method, f.payment_status " +
                       "FROM fees f JOIN students s ON f.student_id = s.student_id ORDER BY f.payment_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("fee_id"),
                    rs.getString("student_name"),
                    rs.getString("room_num"),
                    rs.getDouble("fee_amount"),
                    rs.getDouble("paid_amount"),
                    rs.getDouble("pending_amount"),
                    rs.getDate("payment_date"),
                    rs.getString("payment_method"),
                    rs.getString("payment_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getPendingFeeReport() {
        String[] columnNames = {"Payment ID", "Student Name", "Room", "Phone", "Fee Amount", "Paid Amount", "Pending Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT f.fee_id, s.name as student_name, IFNULL(s.room_number, 'N/A') as room_num, s.phone, f.fee_amount, f.paid_amount, f.pending_amount, f.payment_status " +
                       "FROM fees f JOIN students s ON f.student_id = s.student_id WHERE f.pending_amount > 0 ORDER BY f.pending_amount DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("fee_id"),
                    rs.getString("student_name"),
                    rs.getString("room_num"),
                    rs.getString("phone"),
                    rs.getDouble("fee_amount"),
                    rs.getDouble("paid_amount"),
                    rs.getDouble("pending_amount"),
                    rs.getString("payment_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getAttendanceReport() {
        String[] columnNames = {"ID", "Student Name", "Date", "Check-in", "Check-out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT a.attendance_id, s.name as student_name, a.attendance_date, a.check_in_time, a.check_out_time, a.status " +
                       "FROM attendance a JOIN students s ON a.student_id = s.student_id ORDER BY a.attendance_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("attendance_id"),
                    rs.getString("student_name"),
                    rs.getDate("attendance_date"),
                    rs.getTime("check_in_time") != null ? rs.getTime("check_in_time").toString() : "-",
                    rs.getTime("check_out_time") != null ? rs.getTime("check_out_time").toString() : "-",
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getComplaintReport() {
        String[] columnNames = {"ID", "Student Name", "Type", "Priority", "Status", "Date", "Description", "Resolution"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT c.complaint_id, s.name as student_name, c.complaint_type, c.priority, c.status, c.complaint_date, c.description, c.resolution " +
                       "FROM complaints c JOIN students s ON c.student_id = s.student_id ORDER BY c.complaint_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("complaint_id"),
                    rs.getString("student_name"),
                    rs.getString("complaint_type"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getDate("complaint_date"),
                    rs.getString("description"),
                    rs.getString("resolution") != null ? rs.getString("resolution") : "-"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getVisitorReport() {
        String[] columnNames = {"ID", "Student Name", "Visitor Name", "Relationship", "Phone", "Visit Date", "Entry Time", "Exit Time", "Purpose"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT v.visitor_id, s.name as student_name, v.visitor_name, v.relationship, v.phone, v.visit_date, v.entry_time, v.exit_time, v.purpose " +
                       "FROM visitors v JOIN students s ON v.student_id = s.student_id ORDER BY v.visit_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("visitor_id"),
                    rs.getString("student_name"),
                    rs.getString("visitor_name"),
                    rs.getString("relationship"),
                    rs.getString("phone"),
                    rs.getDate("visit_date"),
                    rs.getTime("entry_time"),
                    rs.getTime("exit_time") != null ? rs.getTime("exit_time").toString() : "-",
                    rs.getString("purpose")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getLeaveReport() {
        String[] columnNames = {"ID", "Student Name", "Leave From", "Leave To", "Reason", "Status", "Approved By"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String query = "SELECT l.leave_id, s.name as student_name, l.leave_from, l.leave_to, l.reason, l.status, l.approved_by " +
                       "FROM leaves l JOIN students s ON l.student_id = s.student_id ORDER BY l.leave_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("leave_id"),
                    rs.getString("student_name"),
                    rs.getDate("leave_from"),
                    rs.getDate("leave_to"),
                    rs.getString("reason"),
                    rs.getString("status"),
                    rs.getString("approved_by") != null ? rs.getString("approved_by") : "-"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}

