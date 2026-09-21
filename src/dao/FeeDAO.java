package dao;

import db.DatabaseConnection;
import model.Fee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {

    public boolean addFee(Fee fee) {
        double pending = Math.max(0, fee.getFeeAmount() - fee.getPaidAmount());
        String status = calculateFeeStatus(fee.getFeeAmount(), fee.getPaidAmount());
        String query = "INSERT INTO fees (student_id, fee_amount, paid_amount, pending_amount, payment_date, payment_method, payment_status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, fee.getStudentId());
            pstmt.setDouble(2, fee.getFeeAmount());
            pstmt.setDouble(3, fee.getPaidAmount());
            pstmt.setDouble(4, pending);
            pstmt.setDate(5, fee.getPaymentDate());
            pstmt.setString(6, fee.getPaymentMethod());
            pstmt.setString(7, status);
            pstmt.setString(8, fee.getRemarks());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFee(Fee fee) {
        double pending = Math.max(0, fee.getFeeAmount() - fee.getPaidAmount());
        String status = calculateFeeStatus(fee.getFeeAmount(), fee.getPaidAmount());
        String query = "UPDATE fees SET student_id=?, fee_amount=?, paid_amount=?, pending_amount=?, payment_date=?, payment_method=?, payment_status=?, remarks=? WHERE fee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, fee.getStudentId());
            pstmt.setDouble(2, fee.getFeeAmount());
            pstmt.setDouble(3, fee.getPaidAmount());
            pstmt.setDouble(4, pending);
            pstmt.setDate(5, fee.getPaymentDate());
            pstmt.setString(6, fee.getPaymentMethod());
            pstmt.setString(7, status);
            pstmt.setString(8, fee.getRemarks());
            pstmt.setInt(9, fee.getFeeId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFee(int feeId) {
        String query = "DELETE FROM fees WHERE fee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, feeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Fee> getAllFees() {
        List<Fee> list = new ArrayList<>();
        String query = "SELECT f.*, s.name as student_name, s.room_number " +
                       "FROM fees f " +
                       "JOIN students s ON f.student_id = s.student_id " +
                       "ORDER BY f.fee_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToFee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Fee> searchFees(String keyword) {
        List<Fee> list = new ArrayList<>();
        String query = "SELECT f.*, s.name as student_name, s.room_number " +
                       "FROM fees f " +
                       "JOIN students s ON f.student_id = s.student_id " +
                       "WHERE s.name LIKE ? OR s.student_code LIKE ? OR f.payment_status LIKE ? OR f.payment_method LIKE ? " +
                       "ORDER BY f.fee_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            pstmt.setString(4, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalPendingFeesSum() {
        String query = "SELECT SUM(pending_amount) FROM fees";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private String calculateFeeStatus(double feeAmount, double paidAmount) {
        if (paidAmount >= feeAmount) {
            return "PAID";
        } else if (paidAmount > 0) {
            return "PARTIAL";
        } else {
            return "PENDING";
        }
    }

    private Fee mapResultSetToFee(ResultSet rs) throws SQLException {
        return new Fee(
            rs.getInt("fee_id"),
            rs.getInt("student_id"),
            rs.getString("student_name"),
            rs.getString("room_number"),
            rs.getDouble("fee_amount"),
            rs.getDouble("paid_amount"),
            rs.getDouble("pending_amount"),
            rs.getDate("payment_date"),
            rs.getString("payment_method"),
            rs.getString("payment_status"),
            rs.getString("remarks")
        );
    }
}

