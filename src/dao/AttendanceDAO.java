package dao;

import db.DatabaseConnection;
import model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean addAttendance(Attendance attendance) {
        String query = "INSERT INTO attendance (student_id, attendance_date, check_in_time, check_out_time, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setDate(2, attendance.getAttendanceDate());
            pstmt.setTime(3, attendance.getCheckInTime());
            pstmt.setTime(4, attendance.getCheckOutTime());
            pstmt.setString(5, attendance.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAttendance(Attendance attendance) {
        String query = "UPDATE attendance SET student_id=?, attendance_date=?, check_in_time=?, check_out_time=?, status=? WHERE attendance_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setDate(2, attendance.getAttendanceDate());
            pstmt.setTime(3, attendance.getCheckInTime());
            pstmt.setTime(4, attendance.getCheckOutTime());
            pstmt.setString(5, attendance.getStatus());
            pstmt.setInt(6, attendance.getAttendanceId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAttendance(int attendanceId) {
        String query = "DELETE FROM attendance WHERE attendance_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attendanceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT a.*, s.name as student_name " +
                       "FROM attendance a " +
                       "JOIN students s ON a.student_id = s.student_id " +
                       "ORDER BY a.attendance_date DESC, a.attendance_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Attendance> searchAttendance(String studentTerm, Date filterDate) {
        List<Attendance> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT a.*, s.name as student_name FROM attendance a JOIN students s ON a.student_id = s.student_id WHERE 1=1 ");

        if (studentTerm != null && !studentTerm.trim().isEmpty()) {
            query.append("AND (s.name LIKE ? OR s.student_code LIKE ? OR a.status LIKE ?) ");
        }
        if (filterDate != null) {
            query.append("AND a.attendance_date = ? ");
        }
        query.append("ORDER BY a.attendance_date DESC, a.attendance_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (studentTerm != null && !studentTerm.trim().isEmpty()) {
                String term = "%" + studentTerm.trim() + "%";
                pstmt.setString(paramIndex++, term);
                pstmt.setString(paramIndex++, term);
                pstmt.setString(paramIndex++, term);
            }
            if (filterDate != null) {
                pstmt.setDate(paramIndex++, filterDate);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        return new Attendance(
            rs.getInt("attendance_id"),
            rs.getInt("student_id"),
            rs.getString("student_name"),
            rs.getDate("attendance_date"),
            rs.getTime("check_in_time"),
            rs.getTime("check_out_time"),
            rs.getString("status")
        );
    }
}

