package com.attendance.adminweb.model;

public record MonthlyAttendanceEmployeeRow(
        String employeeCode,
        String employeeName,
        String role,
        int attendanceDays,
        int lateDays,
        int checkedOutDays,
        String lastAttendanceDate,
        AttendanceState lastState
) {
}
