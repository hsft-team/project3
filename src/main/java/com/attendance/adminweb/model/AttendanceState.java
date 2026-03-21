package com.attendance.adminweb.model;

public enum AttendanceState {
    WORKING("출근"),
    LATE("지각"),
    ABSENT("미출근"),
    CHECKED_OUT("퇴근");

    private final String label;

    AttendanceState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
