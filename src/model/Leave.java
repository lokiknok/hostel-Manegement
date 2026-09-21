package model;

import java.sql.Date;

public class Leave {
    private int leaveId;
    private int studentId;
    private String studentName;
    private Date leaveFrom;
    private Date leaveTo;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED
    private String approvedBy;

    public Leave() {}

    public Leave(int leaveId, int studentId, String studentName, Date leaveFrom, Date leaveTo, String reason, String status, String approvedBy) {
        this.leaveId = leaveId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.leaveFrom = leaveFrom;
        this.leaveTo = leaveTo;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
    }

    public int getLeaveId() { return leaveId; }
    public void setLeaveId(int leaveId) { this.leaveId = leaveId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Date getLeaveFrom() { return leaveFrom; }
    public void setLeaveFrom(Date leaveFrom) { this.leaveFrom = leaveFrom; }

    public Date getLeaveTo() { return leaveTo; }
    public void setLeaveTo(Date leaveTo) { this.leaveTo = leaveTo; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}

