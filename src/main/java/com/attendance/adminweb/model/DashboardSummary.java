package com.attendance.adminweb.model;

public record DashboardSummary(
        int totalEmployees,
        int presentCount,
        int lateCount,
        int absentCount,
        int checkedOutCount
) {
}
