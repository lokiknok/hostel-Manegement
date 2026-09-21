package dao;

import db.DatabaseConnection;
import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (student_code, name, gender, dob, phone, email, department, course, year_of_study, address, parent_name, parent_phone, room_number, admission_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setDate(4, student.getDob());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getDepartment());
            pstmt.setString(8, student.getCourse());
            pstmt.setString(9, student.getYearOfStudy());
            pstmt.setString(10, student.getAddress());
            pstmt.setString(11, student.getParentName());
            pstmt.setString(12, student.getParentPhone());
            pstmt.setString(13, student.getRoomNumber());
            pstmt.setDate(14, student.getAdmissionDate());
            pstmt.setString(15, student.getStatus() != null ? student.getStatus() : "ACTIVE");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET student_code=?, name=?, gender=?, dob=?, phone=?, email=?, department=?, course=?, year_of_study=?, address=?, parent_name=?, parent_phone=?, room_number=?, admission_date=?, status=? WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setDate(4, student.getDob());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getDepartment());
            pstmt.setString(8, student.getCourse());
            pstmt.setString(9, student.getYearOfStudy());
            pstmt.setString(10, student.getAddress());
            pstmt.setString(11, student.getParentName());
            pstmt.setString(12, student.getParentPhone());
            pstmt.setString(13, student.getRoomNumber());
            pstmt.setDate(14, student.getAdmissionDate());
            pstmt.setString(15, student.getStatus());
            pstmt.setInt(16, student.getStudentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY student_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        String query = "SELECT * FROM students WHERE student_code LIKE ? OR name LIKE ? OR department LIKE ? OR phone LIKE ? OR room_number LIKE ? ORDER BY student_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String term = "%" + keyword + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);
            pstmt.setString(3, term);
            pstmt.setString(4, term);
            pstmt.setString(5, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Student getStudentById(int studentId) {
        String query = "SELECT * FROM students WHERE student_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalStudentCount() {
        String query = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("student_id"),
            rs.getString("student_code"),
            rs.getString("name"),
            rs.getString("gender"),
            rs.getDate("dob"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("department"),
            rs.getString("course"),
            rs.getString("year_of_study"),
            rs.getString("address"),
            rs.getString("parent_name"),
            rs.getString("parent_phone"),
            rs.getString("room_number"),
            rs.getDate("admission_date"),
            rs.getString("status")
        );
    }
}

