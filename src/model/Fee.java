package model;

import java.sql.Date;

public class Fee {
    private int feeId;
    private int studentId;
    private String studentName;
    private String roomNumber;
    private double feeAmount;
    private double paidAmount;
    private double pendingAmount;
    private Date paymentDate;
    private String paymentMethod; // Cash, UPI, Card, Bank Transfer
    private String paymentStatus; // PAID, PARTIAL, PENDING
    private String remarks;

    public Fee() {}

    public Fee(int feeId, int studentId, String studentName, String roomNumber, double feeAmount, double paidAmount, double pendingAmount, Date paymentDate, String paymentMethod, String paymentStatus, String remarks) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.feeAmount = feeAmount;
        this.paidAmount = paidAmount;
        this.pendingAmount = feeAmount - paidAmount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = calculatePaymentStatus();
        this.remarks = remarks;
    }

    public String calculatePaymentStatus() {
        if (paidAmount >= feeAmount) {
            return "PAID";
        } else if (paidAmount > 0) {
            return "PARTIAL";
        } else {
            return "PENDING";
        }
    }

    public int getFeeId() { return feeId; }
    public void setFeeId(int feeId) { this.feeId = feeId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public double getFeeAmount() { return feeAmount; }
    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
        this.pendingAmount = this.feeAmount - this.paidAmount;
        this.paymentStatus = calculatePaymentStatus();
    }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
        this.pendingAmount = this.feeAmount - this.paidAmount;
        this.paymentStatus = calculatePaymentStatus();
    }

    public double getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(double pendingAmount) { this.pendingAmount = pendingAmount; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

