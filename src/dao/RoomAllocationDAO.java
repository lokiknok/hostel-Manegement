package dao;

import db.DatabaseConnection;
import model.RoomAllocation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomAllocationDAO {

    public boolean allocateRoom(int studentId, int roomId, String bedNumber, Date allocationDate) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check if student already has active allocation
            String checkStuSql = "SELECT COUNT(*) FROM room_allocations WHERE student_id = ? AND status = 'ALLOCATED'";
            try (PreparedStatement ps = conn.prepareStatement(checkStuSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return false; // Already allocated
                    }
                }
            }

            // 2. Check room capacity & availability
            String checkRoomSql = "SELECT room_number, capacity, occupied_beds FROM rooms WHERE room_id = ? FOR UPDATE";
            String roomNumber = "";
            int capacity = 0, occupied = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkRoomSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roomNumber = rs.getString("room_number");
                        capacity = rs.getInt("capacity");
                        occupied = rs.getInt("occupied_beds");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (occupied >= capacity) {
                conn.rollback();
                return false; // Room full
            }

            // 3. Insert allocation record
            String allocSql = "INSERT INTO room_allocations (student_id, room_id, bed_number, allocation_date, status) VALUES (?, ?, ?, ?, 'ALLOCATED')";
            try (PreparedStatement ps = conn.prepareStatement(allocSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, roomId);
                ps.setString(3, bedNumber);
                ps.setDate(4, allocationDate);
                ps.executeUpdate();
            }

            // 4. Update student's room_number
            String updateStuSql = "UPDATE students SET room_number = ? WHERE student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStuSql)) {
                ps.setString(1, roomNumber);
                ps.setInt(2, studentId);
                ps.executeUpdate();
            }

            // 5. Update room occupied_beds, available_beds, status
            int newOccupied = occupied + 1;
            int newAvail = capacity - newOccupied;
            String newStatus = RoomDAO.calculateRoomStatus(capacity, newOccupied);

            String updateRoomSql = "UPDATE rooms SET occupied_beds = ?, available_beds = ?, status = ? WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateRoomSql)) {
                ps.setInt(1, newOccupied);
                ps.setInt(2, newAvail);
                ps.setString(3, newStatus);
                ps.setInt(4, roomId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean vacateRoom(int studentId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Get active allocation for student
            int roomId = -1;
            int allocId = -1;
            String findSql = "SELECT allocation_id, room_id FROM room_allocations WHERE student_id = ? AND status = 'ALLOCATED'";
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        allocId = rs.getInt("allocation_id");
                        roomId = rs.getInt("room_id");
                    } else {
                        conn.rollback();
                        return false; // No active allocation found
                    }
                }
            }

            // 2. Update allocation status to VACATED
            String updateAllocSql = "UPDATE room_allocations SET status = 'VACATED' WHERE allocation_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateAllocSql)) {
                ps.setInt(1, allocId);
                ps.executeUpdate();
            }

            // 3. Update student room_number to NULL
            String updateStuSql = "UPDATE students SET room_number = NULL WHERE student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStuSql)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            // 4. Update room occupied_beds, available_beds, status
            String checkRoomSql = "SELECT capacity, occupied_beds FROM rooms WHERE room_id = ? FOR UPDATE";
            int capacity = 0, occupied = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkRoomSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        capacity = rs.getInt("capacity");
                        occupied = rs.getInt("occupied_beds");
                    }
                }
            }

            int newOccupied = Math.max(0, occupied - 1);
            int newAvail = capacity - newOccupied;
            String newStatus = RoomDAO.calculateRoomStatus(capacity, newOccupied);

            String updateRoomSql = "UPDATE rooms SET occupied_beds = ?, available_beds = ?, status = ? WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateRoomSql)) {
                ps.setInt(1, newOccupied);
                ps.setInt(2, newAvail);
                ps.setString(3, newStatus);
                ps.setInt(4, roomId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean changeRoom(int studentId, int newRoomId, String newBedNumber, Date changeDate) {
        if (vacateRoom(studentId)) {
            return allocateRoom(studentId, newRoomId, newBedNumber, changeDate);
        }
        return false;
    }

    public List<RoomAllocation> getAllAllocations() {
        List<RoomAllocation> list = new ArrayList<>();
        String query = "SELECT ra.*, s.name as student_name, r.room_number " +
                       "FROM room_allocations ra " +
                       "JOIN students s ON ra.student_id = s.student_id " +
                       "JOIN rooms r ON ra.room_id = r.room_id " +
                       "ORDER BY ra.allocation_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new RoomAllocation(
                    rs.getInt("allocation_id"),
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("bed_number"),
                    rs.getDate("allocation_date"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

