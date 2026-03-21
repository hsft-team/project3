package com.attendance.adminweb.domain.repository;

import com.attendance.adminweb.domain.entity.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    @EntityGraph(attributePaths = "employee")
    List<AttendanceRecord> findAllByEmployeeCompanyIdAndAttendanceDate(Long companyId, LocalDate attendanceDate);

    @EntityGraph(attributePaths = "employee")
    List<AttendanceRecord> findAllByEmployeeCompanyIdAndAttendanceDateBetween(Long companyId,
                                                                               LocalDate startDate,
                                                                               LocalDate endDate);

    boolean existsByEmployeeId(Long employeeId);
}
