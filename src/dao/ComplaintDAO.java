package dao;

import db.DatabaseConnection;
import model.Complaint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    public boolean addComplaint(Complaint complaint) {
        String query = "INSERT INTO complaints (student_id, complaint_type, description, complaint_date, priority, status, resolution) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, complaint.getStudentId());
            pstmt.setString(2, complaint.getComplaintType());
            pstmt.setString(3, complaint.getDescription());
            pstmt.setDate(4, complaint.getComplaintDate());
            pstmt.setString(5, complaint.getPriority());
            pstmt.setString(6, complaint.getStatus() != null ? complaint.getStatus() : "PENDING");
            pstmt.setString(7, complaint.getResolution());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateComplaintStatus(int complaintId, String status, String resolution) {
        String query = "UPDATE complaints SET status=?, resolution=? WHERE complaint_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setString(2, resolution);
            pstmt.setInt(3, complaintId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteComplaint(int complaintId) {
        String query = "DELETE FROM complaints WHERE complaint_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Complaint> getAllComplaints() {
        List<Complaint> list = new ArrayList<>();
        String query = "SELECT c.*, s.name as student_name " +
                       "FROM complaints c " +
                       "JOIN students s ON c.student_id = s.student_id " +
                       "ORDER BY c.complaint_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToComplaint(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Complaint> searchComplaints(String keyword) {
        List<Complaint> list = new ArrayList<>();
        String query = "SELECT c.*, s.name as student_name " +
                       "FROM complaints c " +
                       "JOIN students s ON c.student_id = s.student_id " +
                       "WHERE s.name LIKE ? OR c.complaint_type LIKE ? OR c.priority LIKE ? OR c.status LIKE ? " +
                       "ORDER BY c.complaint_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            pstmt.setString(4, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalComplaintsCount() {
        String query = "SELECT COUNT(*) FROM complaints";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Complaint mapResultSetToComplaint(ResultSet rs) throws SQLException {
        return new Complaint(
            rs.getInt("complaint_id"),
            rs.getInt("student_id"),
            rs.getString("student_name"),
            rs.getString("complaint_type"),
            rs.getString("description"),
            rs.getDate("complaint_date"),
            rs.getString("priority"),
            rs.getString("status"),
            rs.getString("resolution")
        );
    }
}

