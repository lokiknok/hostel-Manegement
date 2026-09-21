# Hostel Management System (Java Swing + MySQL)

A complete, beginner-friendly yet professional desktop application built in **Java** using **Java Swing** for GUI, **MySQL** for database storage, and **JDBC** for database connectivity following an MVC-like architecture.

---

## 📌 Project Overview

The **Hostel Management System** simplifies hostel operations for administrators by automating student management, room allocation, fee tracking, attendance, complaint resolution, visitor logs, and leave management.

---

## ✨ Features

1. **Authentication & Login**
   - User authentication via MySQL database.
   - User-friendly error messages for invalid credentials.

2. **Dashboard Overview**
   - Real-time statistics: Total Students, Total Rooms, Available Rooms, Occupied Rooms, Pending Fees, Total Complaints.
   - Quick action shortcuts and sidebar navigation.

3. **Student Management**
   - Full CRUD operations (Add, Edit, Delete, View, Search).
   - Validation for student code, name, phone, and email formats.
   - JTable presentation of student records.

4. **Room Management**
   - Track room capacity, occupied beds, and available beds.
   - Automatic status updates (`AVAILABLE`, `PARTIALLY OCCUPIED`, `FULL`).
   - Prevents student allocation to full rooms.

5. **Student Room Allocation**
   - Allocate, Change, and Vacate rooms with bed assignment.
   - Automatic room capacity adjustment and student record link.
   - Prevents duplicate active allocations.

6. **Fee Management**
   - Log payments with multiple payment methods (Cash, UPI, Card, Bank Transfer).
   - Automatic pending fee calculation (`Fee Amount - Paid Amount`).
   - Payment status tracking (`PAID`, `PARTIAL`, `PENDING`).

7. **Attendance Management**
   - Record daily attendance with check-in and check-out timestamps.
   - Status options: `PRESENT`, `ABSENT`, `LEAVE`.
   - Filter attendance by student name or date.

8. **Complaint Management**
   - Log complaints with categories (Electrical, Plumbing, Food, Room, Cleaning, Internet, Other) and priority (`LOW`, `MEDIUM`, `HIGH`).
   - Administrator update of complaint resolution and status (`PENDING`, `IN PROGRESS`, `RESOLVED`).

9. **Visitor Management**
   - Maintain visitor logs with student relation, contact info, visit date, entry/exit times, and purpose.

10. **Leave Management**
    - Student leave requests with approval/rejection capability (`PENDING`, `APPROVED`, `REJECTED`) and approval log.

11. **Reports & CSV Export**
    - Filterable reports for 8 operational categories.
    - Export any report directly to `.csv` spreadsheet files.

---

## 🛠️ Technology Stack

* **Programming Language**: Java 8+
* **GUI Framework**: Java Swing & AWT
* **Database**: MySQL 5.7 / 8.x
* **Database Connectivity**: JDBC (`PreparedStatement`)
* **Architecture**: Model-DAO-UI (MVC Pattern)

---

## 📂 Project Structure

```
d:\vinoth\
├── lib\
│   └── mysql-connector-j-8.3.0.jar
├── src\
│   ├── db\
│   │   └── DatabaseConnection.java
│   ├── model\
│   │   ├── User.java
│   │   ├── Student.java
│   │   ├── Room.java
│   │   ├── RoomAllocation.java
│   │   ├── Fee.java
│   │   ├── Attendance.java
│   │   ├── Complaint.java
│   │   ├── Visitor.java
│   │   └── Leave.java
│   ├── dao\
│   │   ├── UserDAO.java
│   │   ├── StudentDAO.java
│   │   ├── RoomDAO.java
│   │   ├── RoomAllocationDAO.java
│   │   ├── FeeDAO.java
│   │   ├── AttendanceDAO.java
│   │   ├── ComplaintDAO.java
│   │   ├── VisitorDAO.java
│   │   ├── LeaveDAO.java
│   │   └── ReportDAO.java
│   ├── ui\
│   │   ├── UIUtils.java
│   │   ├── LoginFrame.java
│   │   ├── DashboardFrame.java
│   │   ├── StudentPanel.java
│   │   ├── RoomPanel.java
│   │   ├── AllocationPanel.java
│   │   ├── FeePanel.java
│   │   ├── AttendancePanel.java
│   │   ├── ComplaintPanel.java
│   │   ├── VisitorPanel.java
│   │   ├── LeavePanel.java
│   │   └── ReportPanel.java
│   └── Main.java
├── database.sql
└── README.md
```

---

## 🔑 Default Login Credentials

* **Username**: `admin`
* **Password**: `admin123`

---

## 🗄️ Database Setup Instructions

1. Start your **MySQL Server** (e.g., via XAMPP, WAMP, or standalone MySQL service).
2. Open MySQL CLI or MySQL Workbench and run:
   ```sql
   SOURCE d:/vinoth/database.sql;
   ```
   Or import `database.sql` into your MySQL server.
3. Configure your MySQL credentials in `src/db/DatabaseConnection.java` if different from default:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/hostel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
   private static final String USER = "root";
   private static final String PASSWORD = "admin";
   ```

---

## 🚀 How to Run the Application

### Using PowerShell / Command Prompt

1. **Compile the project**:
   ```powershell
   if (!(Test-Path bin)) { New-Item -ItemType Directory -Path bin }
   javac -cp "lib/mysql-connector-j-8.3.0.jar;src" -d bin src/model/*.java src/db/*.java src/dao/*.java src/ui/*.java src/Main.java
   ```

2. **Run the project**:
   ```powershell
   java -cp "lib/mysql-connector-j-8.3.0.jar;bin" Main
   ```

---

## 🖼️ Screenshots

*(Add application screenshots here for college presentation/demo)*

