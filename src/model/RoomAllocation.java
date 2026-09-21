package model;

import java.sql.Date;

public class RoomAllocation {
    private int allocationId;
    private int studentId;
    private String studentName;
    private int roomId;
    private String roomNumber;
    private String bedNumber;
    private Date allocationDate;
    private String status; // ALLOCATED, VACATED

    public RoomAllocation() {}

    public RoomAllocation(int allocationId, int studentId, String studentName, int roomId, String roomNumber, String bedNumber, Date allocationDate, String status) {
        this.allocationId = allocationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.bedNumber = bedNumber;
        this.allocationDate = allocationDate;
        this.status = status;
    }

    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public Date getAllocationDate() { return allocationDate; }
    public void setAllocationDate(Date allocationDate) { this.allocationDate = allocationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

