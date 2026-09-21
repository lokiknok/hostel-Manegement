package model;

import java.sql.Date;

public class Complaint {
    private int complaintId;
    private int studentId;
    private String studentName;
    private String complaintType; // Electrical, Plumbing, Food, Room, Cleaning, Internet, Other
    private String description;
    private Date complaintDate;
    private String priority; // LOW, MEDIUM, HIGH
    private String status; // PENDING, IN PROGRESS, RESOLVED
    private String resolution;

    public Complaint() {}

    public Complaint(int complaintId, int studentId, String studentName, String complaintType, String description, Date complaintDate, String priority, String status, String resolution) {
        this.complaintId = complaintId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.complaintType = complaintType;
        this.description = description;
        this.complaintDate = complaintDate;
        this.priority = priority;
        this.status = status;
        this.resolution = resolution;
    }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getComplaintDate() { return complaintDate; }
    public void setComplaintDate(Date complaintDate) { this.complaintDate = complaintDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}

