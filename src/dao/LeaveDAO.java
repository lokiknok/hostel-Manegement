package dao;

import db.DatabaseConnection;
import model.Leave;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    public boolean addLeave(Leave leave) {
        String query = "INSERT INTO leaves (student_id, leave_from, leave_to, reason, status, approved_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, leave.getStudentId());
            pstmt.setDate(2, leave.getLeaveFrom());
            pstmt.setDate(3, leave.getLeaveTo());
            pstmt.setString(4, leave.getReason());
            pstmt.setString(5, leave.getStatus() != null ? leave.getStatus() : "PENDING");
            pstmt.setString(6, leave.getApprovedBy());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLeaveStatus(int leaveId, String status, String approvedBy) {
        String query = "UPDATE leaves SET status=?, approved_by=? WHERE leave_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setString(2, approvedBy);
            pstmt.setInt(3, leaveId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLeave(int leaveId) {
        String query = "DELETE FROM leaves WHERE leave_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, leaveId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Leave> getAllLeaves() {
        List<Leave> list = new ArrayList<>();
        String query = "SELECT l.*, s.name as student_name " +
                       "FROM leaves l " +
                       "JOIN students s ON l.student_id = s.student_id " +
                       "ORDER BY l.leave_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToLeave(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Leave> searchLeaves(String keyword) {
        List<Leave> list = new ArrayList<>();
        String query = "SELECT l.*, s.name as student_name " +
                       "FROM leaves l " +
                       "JOIN students s ON l.student_id = s.student_id " +
                       "WHERE s.name LIKE ? OR l.status LIKE ? OR l.reason LIKE ? " +
                       "ORDER BY l.leave_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLeave(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Leave mapResultSetToLeave(ResultSet rs) throws SQLException {
        return new Leave(
            rs.getInt("leave_id"),
            rs.getInt("student_id"),
            rs.getString("student_name"),
            rs.getDate("leave_from"),
            rs.getDate("leave_to"),
            rs.getString("reason"),
            rs.getString("status"),
            rs.getString("approved_by")
        );
    }
}

