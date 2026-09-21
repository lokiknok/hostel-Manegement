package model;

import java.sql.Date;
import java.sql.Time;

public class Attendance {
    private int attendanceId;
    private int studentId;
    private String studentName;
    private Date attendanceDate;
    private Time checkInTime;
    private Time checkOutTime;
    private String status; // PRESENT, ABSENT, LEAVE

    public Attendance() {}

    public Attendance(int attendanceId, int studentId, String studentName, Date attendanceDate, Time checkInTime, Time checkOutTime, String status) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.attendanceDate = attendanceDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public Time getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Time checkInTime) { this.checkInTime = checkInTime; }

    public Time getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(Time checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

