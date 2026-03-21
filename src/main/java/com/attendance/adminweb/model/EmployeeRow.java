package com.attendance.adminweb.model;

public record EmployeeRow(
        Long id,
        String employeeCode,
        String name,
        String role,
        String companyName,
        AttendanceState attendanceState,
        String checkInTime
) {
}
