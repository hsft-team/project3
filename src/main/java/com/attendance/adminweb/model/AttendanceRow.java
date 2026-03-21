package com.attendance.adminweb.model;

public record AttendanceRow(
        String employeeCode,
        String employeeName,
        String role,
        AttendanceState state,
        String checkInTime,
        String checkOutTime,
        String note
) {
}
