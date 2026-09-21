-- =========================================================
-- Hostel Management System - Database Schema & Sample Data
-- Database Name: hostel_management
-- =========================================================

CREATE DATABASE IF NOT EXISTS hostel_management;
USE hostel_management;

-- 1. Users Table for Authentication
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Rooms Table
DROP TABLE IF EXISTS rooms;
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    floor INT NOT NULL,
    room_type VARCHAR(20) NOT NULL, -- Single, Double, Triple, Four Sharing
    capacity INT NOT NULL,
    occupied_beds INT DEFAULT 0,
    available_beds INT NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE' -- AVAILABLE, PARTIALLY OCCUPIED, FULL
);

-- 3. Students Table
DROP TABLE IF EXISTS students;
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    dob DATE,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    course VARCHAR(50),
    year_of_study VARCHAR(20),
    address TEXT,
    parent_name VARCHAR(100),
    parent_phone VARCHAR(15),
    room_number VARCHAR(20) DEFAULT NULL,
    admission_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE' -- ACTIVE, INACTIVE
);

-- 4. Room Allocations Table
DROP TABLE IF EXISTS room_allocations;
CREATE TABLE room_allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    bed_number VARCHAR(10),
    allocation_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ALLOCATED', -- ALLOCATED, VACATED
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- 5. Fees Table
DROP TABLE IF EXISTS fees;
CREATE TABLE fees (
    fee_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    fee_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    pending_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(30) NOT NULL, -- Cash, UPI, Card, Bank Transfer
    payment_status VARCHAR(20) NOT NULL, -- PAID, PARTIAL, PENDING
    remarks VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 6. Attendance Table
DROP TABLE IF EXISTS attendance;
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    status VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LEAVE
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 7. Complaints Table
DROP TABLE IF EXISTS complaints;
CREATE TABLE complaints (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    complaint_type VARCHAR(50) NOT NULL, -- Electrical, Plumbing, Food, Room, Cleaning, Internet, Other
    description TEXT NOT NULL,
    complaint_date DATE NOT NULL,
    priority VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, IN PROGRESS, RESOLVED
    resolution TEXT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 8. Visitors Table
DROP TABLE IF EXISTS visitors;
CREATE TABLE visitors (
    visitor_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    visitor_name VARCHAR(100) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    visit_date DATE NOT NULL,
    entry_time TIME NOT NULL,
    exit_time TIME,
    purpose VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 9. Leaves Table
DROP TABLE IF EXISTS leaves;
CREATE TABLE leaves (
    leave_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    leave_from DATE NOT NULL,
    leave_to DATE NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    approved_by VARCHAR(50),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- =========================================================
-- Sample Seed Data
-- =========================================================

-- Insert Admin User (Username: admin, Password: admin123)
INSERT INTO users (username, password, full_name, role) 
VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN');

-- Insert Sample Rooms
INSERT INTO rooms (room_number, floor, room_type, capacity, occupied_beds, available_beds, status) VALUES
('101', 1, 'Single', 1, 1, 0, 'FULL'),
('102', 1, 'Double', 2, 1, 1, 'PARTIALLY OCCUPIED'),
('103', 1, 'Double', 2, 0, 2, 'AVAILABLE'),
('201', 2, 'Triple', 3, 2, 1, 'PARTIALLY OCCUPIED'),
('202', 2, 'Four Sharing', 4, 0, 4, 'AVAILABLE');

-- Insert Sample Students
INSERT INTO students (student_code, name, gender, dob, phone, email, department, course, year_of_study, address, parent_name, parent_phone, room_number, admission_date, status) VALUES
('STU1001', 'John Doe', 'Male', '2002-05-15', '9876543210', 'john.doe@example.com', 'Computer Science', 'B.Tech', '3rd Year', '123 Main St, New York', 'Robert Doe', '9876543211', '101', '2023-08-01', 'ACTIVE'),
('STU1002', 'Jane Smith', 'Female', '2003-09-20', '9876543220', 'jane.smith@example.com', 'Information Tech', 'B.Tech', '2nd Year', '456 Elm St, Boston', 'David Smith', '9876543221', '102', '2023-08-01', 'ACTIVE'),
('STU1003', 'Alex Johnson', 'Male', '2001-12-10', '9876543230', 'alex.j@example.com', 'Mechanical Eng', 'B.Tech', '4th Year', '789 Oak St, Chicago', 'Michael Johnson', '9876543231', '201', '2022-08-01', 'ACTIVE'),
('STU1004', 'Emily Davis', 'Female', '2002-03-25', '9876543240', 'emily.d@example.com', 'Electrical Eng', 'B.Tech', '3rd Year', '321 Pine St, Houston', 'James Davis', '9876543241', '201', '2023-08-01', 'ACTIVE');

-- Insert Room Allocations
INSERT INTO room_allocations (student_id, room_id, bed_number, allocation_date, status) VALUES
(1, 1, 'Bed-1', '2023-08-01', 'ALLOCATED'),
(2, 2, 'Bed-1', '2023-08-01', 'ALLOCATED'),
(3, 4, 'Bed-1', '2022-08-01', 'ALLOCATED'),
(4, 4, 'Bed-2', '2023-08-01', 'ALLOCATED');

-- Insert Sample Fee Records
INSERT INTO fees (student_id, fee_amount, paid_amount, pending_amount, payment_date, payment_method, payment_status, remarks) VALUES
(1, 50000.00, 50000.00, 0.00, '2023-08-05', 'UPI', 'PAID', 'Full Semester Payment'),
(2, 50000.00, 30000.00, 20000.00, '2023-08-06', 'Bank Transfer', 'PARTIAL', '1st Installment Paid'),
(3, 55000.00, 0.00, 55000.00, '2023-08-01', 'Cash', 'PENDING', 'Fee Due'),
(4, 50000.00, 50000.00, 0.00, '2023-08-07', 'Card', 'PAID', 'Semester Fee Paid');

-- Insert Sample Attendance
INSERT INTO attendance (student_id, attendance_date, check_in_time, check_out_time, status) VALUES
(1, CURRENT_DATE(), '08:00:00', '21:30:00', 'PRESENT'),
(2, CURRENT_DATE(), '08:15:00', '21:00:00', 'PRESENT'),
(3, CURRENT_DATE(), NULL, NULL, 'ABSENT'),
(4, CURRENT_DATE(), NULL, NULL, 'LEAVE');

-- Insert Sample Complaints
INSERT INTO complaints (student_id, complaint_type, description, complaint_date, priority, status, resolution) VALUES
(2, 'Plumbing', 'Water leakage in room 102 washroom.', '2023-09-10', 'HIGH', 'PENDING', NULL),
(3, 'Internet', 'Wi-Fi connection slow in second floor.', '2023-09-12', 'MEDIUM', 'IN PROGRESS', 'IT team inspecting router'),
(1, 'Electrical', 'Study lamp socket loose in room 101.', '2023-09-08', 'LOW', 'RESOLVED', 'Socket replaced by electrician');

-- Insert Sample Visitors
INSERT INTO visitors (student_id, visitor_name, relationship, phone, visit_date, entry_time, exit_time, purpose) VALUES
(1, 'Robert Doe', 'Father', '9876543211', CURRENT_DATE(), '10:00:00', '12:30:00', 'Delivered books & clothing'),
(2, 'Sarah Smith', 'Mother', '9876543222', CURRENT_DATE(), '14:00:00', '16:00:00', 'Routine weekend visit');

-- Insert Sample Leaves
INSERT INTO leaves (student_id, leave_from, leave_to, reason, status, approved_by) VALUES
(4, CURRENT_DATE(), DATE_ADD(CURRENT_DATE(), INTERVAL 3 DAY), 'Family emergency at home', 'APPROVED', 'admin'),
(3, DATE_ADD(CURRENT_DATE(), INTERVAL 5 DAY), DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 'Attending Tech Fest', 'PENDING', NULL);

