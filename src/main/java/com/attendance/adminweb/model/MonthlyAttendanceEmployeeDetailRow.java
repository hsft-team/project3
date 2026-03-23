package com.attendance.adminweb.model;

import java.util.List;

public record MonthlyAttendanceEmployeeDetailRow(
        String employeeCode,
        String employeeName,
        String role,
        int attendanceDays,
        int lateDays,
        int checkedOutDays,
        List<MonthlyAttendanceRecordRow> records
) {
}
