package com.attendance.adminweb.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRoleConstraintUpdater {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRoleConstraintUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateEmployeeRoleConstraint() {
        jdbcTemplate.execute("alter table employees drop constraint if exists employees_role_check");
        jdbcTemplate.execute(
                "alter table employees add constraint employees_role_check " +
                        "check (role in ('ADMIN', 'WORKPLACE_ADMIN', 'EMPLOYEE'))"
        );
    }
}
