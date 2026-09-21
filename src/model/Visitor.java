package model;

import java.sql.Date;
import java.sql.Time;

public class Visitor {
    private int visitorId;
    private int studentId;
    private String studentName;
    private String visitorName;
    private String relationship;
    private String phone;
    private Date visitDate;
    private Time entryTime;
    private Time exitTime;
    private String purpose;

    public Visitor() {}

    public Visitor(int visitorId, int studentId, String studentName, String visitorName, String relationship, String phone, Date visitDate, Time entryTime, Time exitTime, String purpose) {
        this.visitorId = visitorId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.visitorName = visitorName;
        this.relationship = relationship;
        this.phone = phone;
        this.visitDate = visitDate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.purpose = purpose;
    }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Date getVisitDate() { return visitDate; }
    public void setVisitDate(Date visitDate) { this.visitDate = visitDate; }

    public Time getEntryTime() { return entryTime; }
    public void setEntryTime(Time entryTime) { this.entryTime = entryTime; }

    public Time getExitTime() { return exitTime; }
    public void setExitTime(Time exitTime) { this.exitTime = exitTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}

