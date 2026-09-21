package dao;

import db.DatabaseConnection;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean addRoom(Room room) {
        int avail = room.getCapacity() - room.getOccupiedBeds();
        String status = calculateRoomStatus(room.getCapacity(), room.getOccupiedBeds());
        String query = "INSERT INTO rooms (room_number, floor, room_type, capacity, occupied_beds, available_beds, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getFloor());
            pstmt.setString(3, room.getRoomType());
            pstmt.setInt(4, room.getCapacity());
            pstmt.setInt(5, room.getOccupiedBeds());
            pstmt.setInt(6, avail);
            pstmt.setString(7, status);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoom(Room room) {
        int avail = room.getCapacity() - room.getOccupiedBeds();
        String status = calculateRoomStatus(room.getCapacity(), room.getOccupiedBeds());
        String query = "UPDATE rooms SET room_number=?, floor=?, room_type=?, capacity=?, occupied_beds=?, available_beds=?, status=? WHERE room_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getFloor());
            pstmt.setString(3, room.getRoomType());
            pstmt.setInt(4, room.getCapacity());
            pstmt.setInt(5, room.getOccupiedBeds());
            pstmt.setInt(6, avail);
            pstmt.setString(7, status);
            pstmt.setInt(8, room.getRoomId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE room_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM rooms ORDER BY room_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> getAvailableRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM rooms WHERE available_beds > 0 ORDER BY room_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Room getRoomById(int roomId) {
        String query = "SELECT * FROM rooms WHERE room_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String calculateRoomStatus(int capacity, int occupied) {
        if (occupied >= capacity) {
            return "FULL";
        } else if (occupied > 0) {
            return "PARTIALLY OCCUPIED";
        } else {
            return "AVAILABLE";
        }
    }

    public int getTotalRoomsCount() {
        String query = "SELECT COUNT(*) FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getAvailableRoomsCount() {
        String query = "SELECT COUNT(*) FROM rooms WHERE status = 'AVAILABLE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getOccupiedRoomsCount() {
        String query = "SELECT COUNT(*) FROM rooms WHERE status IN ('FULL', 'PARTIALLY OCCUPIED')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("room_id"),
            rs.getString("room_number"),
            rs.getInt("floor"),
            rs.getString("room_type"),
            rs.getInt("capacity"),
            rs.getInt("occupied_beds"),
            rs.getInt("available_beds"),
            rs.getString("status")
        );
    }
}

