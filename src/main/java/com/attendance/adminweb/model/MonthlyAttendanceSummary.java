package com.attendance.adminweb.model;

public record MonthlyAttendanceSummary(
        String monthLabel,
        int totalEmployees,
        int attendedEmployees,
        int attendanceCount,
        int lateCount,
        int checkedOutCount
) {
}
