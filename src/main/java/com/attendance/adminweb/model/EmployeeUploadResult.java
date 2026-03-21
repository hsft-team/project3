package com.attendance.adminweb.model;

import java.util.List;

public record EmployeeUploadResult(
        int successCount,
        List<String> failureMessages
) {
    public int failureCount() {
        return failureMessages.size();
    }

    public boolean hasFailures() {
        return !failureMessages.isEmpty();
    }
}
