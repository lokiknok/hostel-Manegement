package model;

import java.sql.Date;

public class Student {
    private int studentId;
    private String studentCode;
    private String name;
    private String gender;
    private Date dob;
    private String phone;
    private String email;
    private String department;
    private String course;
    private String yearOfStudy;
    private String address;
    private String parentName;
    private String parentPhone;
    private String roomNumber;
    private Date admissionDate;
    private String status;

    public Student() {}

    public Student(int studentId, String studentCode, String name, String gender, Date dob, String phone,
                   String email, String department, String course, String yearOfStudy, String address,
                   String parentName, String parentPhone, String roomNumber, Date admissionDate, String status) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.department = department;
        this.course = course;
        this.yearOfStudy = yearOfStudy;
        this.address = address;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.roomNumber = roomNumber;
        this.admissionDate = admissionDate;
        this.status = status;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Date getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(Date admissionDate) { this.admissionDate = admissionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return name + " (" + (studentCode != null ? studentCode : ("ID:" + studentId)) + ")";
    }
}

