package dao;

import db.DatabaseConnection;
import model.Visitor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitorDAO {

    public boolean addVisitor(Visitor visitor) {
        String query = "INSERT INTO visitors (student_id, visitor_name, relationship, phone, visit_date, entry_time, exit_time, purpose) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, visitor.getStudentId());
            pstmt.setString(2, visitor.getVisitorName());
            pstmt.setString(3, visitor.getRelationship());
            pstmt.setString(4, visitor.getPhone());
            pstmt.setDate(5, visitor.getVisitDate());
            pstmt.setTime(6, visitor.getEntryTime());
            pstmt.setTime(7, visitor.getExitTime());
            pstmt.setString(8, visitor.getPurpose());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateVisitor(Visitor visitor) {
        String query = "UPDATE visitors SET student_id=?, visitor_name=?, relationship=?, phone=?, visit_date=?, entry_time=?, exit_time=?, purpose=? WHERE visitor_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, visitor.getStudentId());
            pstmt.setString(2, visitor.getVisitorName());
            pstmt.setString(3, visitor.getRelationship());
            pstmt.setString(4, visitor.getPhone());
            pstmt.setDate(5, visitor.getVisitDate());
            pstmt.setTime(6, visitor.getEntryTime());
            pstmt.setTime(7, visitor.getExitTime());
            pstmt.setString(8, visitor.getPurpose());
            pstmt.setInt(9, visitor.getVisitorId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVisitor(int visitorId) {
        String query = "DELETE FROM visitors WHERE visitor_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, visitorId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Visitor> getAllVisitors() {
        List<Visitor> list = new ArrayList<>();
        String query = "SELECT v.*, s.name as student_name " +
                       "FROM visitors v " +
                       "JOIN students s ON v.student_id = s.student_id " +
                       "ORDER BY v.visit_date DESC, v.visitor_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToVisitor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Visitor> searchVisitors(String keyword) {
        List<Visitor> list = new ArrayList<>();
        String query = "SELECT v.*, s.name as student_name " +
                       "FROM visitors v " +
                       "JOIN students s ON v.student_id = s.student_id " +
                       "WHERE v.visitor_name LIKE ? OR s.name LIKE ? OR v.relationship LIKE ? OR v.phone LIKE ? " +
                       "ORDER BY v.visit_date DESC, v.visitor_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            pstmt.setString(4, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVisitor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Visitor mapResultSetToVisitor(ResultSet rs) throws SQLException {
        return new Visitor(
            rs.getInt("visitor_id"),
            rs.getInt("student_id"),
            rs.getString("student_name"),
            rs.getString("visitor_name"),
            rs.getString("relationship"),
            rs.getString("phone"),
            rs.getDate("visit_date"),
            rs.getTime("entry_time"),
            rs.getTime("exit_time"),
            rs.getString("purpose")
        );
    }
}

