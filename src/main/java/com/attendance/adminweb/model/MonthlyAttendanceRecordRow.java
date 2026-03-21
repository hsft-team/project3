package com.attendance.adminweb.model;

public record MonthlyAttendanceRecordRow(
        String attendanceDate,
        String employeeCode,
        String employeeName,
        String role,
        AttendanceState state,
        String checkInTime,
        String checkOutTime,
        String note
) {
}
