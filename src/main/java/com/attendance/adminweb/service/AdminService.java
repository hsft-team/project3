package com.attendance.adminweb.service;

import com.attendance.adminweb.domain.entity.AttendanceRecord;
import com.attendance.adminweb.domain.entity.AttendanceStatus;
import com.attendance.adminweb.domain.entity.Company;
import com.attendance.adminweb.domain.entity.CompanySetting;
import com.attendance.adminweb.domain.entity.Employee;
import com.attendance.adminweb.domain.entity.EmployeeRole;
import com.attendance.adminweb.domain.repository.AttendanceRecordRepository;
import com.attendance.adminweb.domain.repository.CompanyRepository;
import com.attendance.adminweb.domain.repository.CompanySettingRepository;
import com.attendance.adminweb.domain.repository.EmployeeRepository;
import com.attendance.adminweb.model.AttendanceRow;
import com.attendance.adminweb.model.AttendanceState;
import com.attendance.adminweb.model.CompanyLocationForm;
import com.attendance.adminweb.model.CompanyLocationView;
import com.attendance.adminweb.model.DashboardSummary;
import com.attendance.adminweb.model.EmployeeForm;
import com.attendance.adminweb.model.EmployeeRow;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final EmployeeRepository employeeRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CompanyRepository companyRepository;
    private final CompanySettingRepository companySettingRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(EmployeeRepository employeeRepository,
                        AttendanceRecordRepository attendanceRecordRepository,
                        CompanyRepository companyRepository,
                        CompanySettingRepository companySettingRepository,
                        PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.companyRepository = companyRepository;
        this.companySettingRepository = companySettingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DashboardSummary getTodaySummary(String employeeCode) {
        List<Employee> employees = getCompanyEmployees(employeeCode);
        Map<Long, AttendanceRecord> recordsByEmployeeId = getTodayRecordsByEmployee(employees);

        int total = employees.size();
        int present = 0;
        int late = 0;
        int absent = 0;
        int checkedOut = 0;

        for (Employee employee : employees) {
            AttendanceRecord record = recordsByEmployeeId.get(employee.getId());
            if (record == null) {
                absent++;
                continue;
            }

            if (record.isLate()) {
                late++;
            } else if (record.getStatus() == AttendanceStatus.CHECKED_OUT) {
                checkedOut++;
            } else {
                present++;
            }
        }

        return new DashboardSummary(total, present, late, absent, checkedOut);
    }

    public List<AttendanceRow> getTodayAttendances(String employeeCode) {
        List<Employee> employees = getCompanyEmployees(employeeCode);
        Map<Long, AttendanceRecord> recordsByEmployeeId = getTodayRecordsByEmployee(employees);

        return employees.stream()
                .map(employee -> {
                    AttendanceRecord record = recordsByEmployeeId.get(employee.getId());
                    AttendanceState state = toState(record);
                    return new AttendanceRow(
                            employee.getEmployeeCode(),
                            employee.getName(),
                            employee.getRole().name(),
                            state,
                            formatCheckIn(record),
                            formatCheckOut(record),
                            buildNote(record)
                    );
                })
                .toList();
    }

    public List<EmployeeRow> getEmployees(String employeeCode) {
        List<Employee> employees = getCompanyEmployees(employeeCode);
        Map<Long, AttendanceRecord> recordsByEmployeeId = getTodayRecordsByEmployee(employees);

        return employees.stream()
                .map(employee -> {
                    AttendanceRecord record = recordsByEmployeeId.get(employee.getId());
                    return new EmployeeRow(
                            employee.getId(),
                            employee.getEmployeeCode(),
                            employee.getName(),
                            employee.getRole().name(),
                            employee.getCompany().getName(),
                            toState(record),
                            formatCheckIn(record)
                    );
                })
                .toList();
    }

    public EmployeeForm getEmployeeFormForCreate() {
        EmployeeForm form = new EmployeeForm();
        form.setRole(EmployeeRole.EMPLOYEE.name());
        form.setPassword("");
        return form;
    }

    public EmployeeForm getEmployeeFormForEdit(String employeeCode, Long employeeId) {
        Employee employee = getEditableEmployee(employeeCode, employeeId);
        return EmployeeForm.from(employee);
    }

    public CompanyLocationView getCompanyLocation(String employeeCode) {
        Employee admin = getEmployeeByCode(employeeCode);
        Company company = admin.getCompany();
        CompanySetting setting = companySettingRepository.findByCompany(company)
                .orElseThrow(() -> new EntityNotFoundException("회사 설정을 찾을 수 없습니다."));

        return new CompanyLocationView(
                company.getName(),
                company.getLatitude(),
                company.getLongitude(),
                setting.getAllowedRadiusMeters(),
                setting.getLateAfterTime().format(TIME_FORMATTER)
        );
    }

    public CompanyLocationForm getCompanyLocationForm(String employeeCode) {
        CompanyLocationView location = getCompanyLocation(employeeCode);
        CompanyLocationForm form = new CompanyLocationForm();
        form.setCompanyName(location.companyName());
        form.setLatitude(location.latitude());
        form.setLongitude(location.longitude());
        form.setAllowedRadiusMeters(location.allowedRadiusMeters());
        form.setLateAfterTime(location.lateAfterTime());
        return form;
    }

    @Transactional
    public void updateCompanyLocation(String employeeCode, CompanyLocationForm form) {
        Employee admin = getEmployeeByCode(employeeCode);
        Company company = companyRepository.findById(admin.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("회사를 찾을 수 없습니다."));
        CompanySetting setting = companySettingRepository.findByCompany(company)
                .orElseThrow(() -> new EntityNotFoundException("회사 설정을 찾을 수 없습니다."));

        company.updateLocation(form.getLatitude(), form.getLongitude());
        setting.updateAllowedRadiusMeters(form.getAllowedRadiusMeters());
    }

    @Transactional
    public void createEmployee(String adminEmployeeCode, EmployeeForm form) {
        Employee admin = getEmployeeByCode(adminEmployeeCode);
        validateDuplicateEmployeeCode(form.getEmployeeCode(), null);

        Employee employee = new Employee(
                form.getEmployeeCode().trim(),
                form.getName().trim(),
                passwordEncoder.encode(form.getPassword()),
                form.getEmployeeRole(),
                admin.getCompany()
        );
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(String adminEmployeeCode, Long employeeId, EmployeeForm form) {
        Employee employee = getEditableEmployee(adminEmployeeCode, employeeId);
        validateDuplicateEmployeeCode(form.getEmployeeCode(), employeeId);

        employee.updateProfile(
                form.getEmployeeCode().trim(),
                form.getName().trim(),
                form.getEmployeeRole()
        );

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            employee.updatePassword(passwordEncoder.encode(form.getPassword()));
        }
    }

    @Transactional
    public void deleteEmployee(String adminEmployeeCode, Long employeeId) {
        Employee admin = getEmployeeByCode(adminEmployeeCode);
        Employee employee = getEditableEmployee(adminEmployeeCode, employeeId);

        if (admin.getId().equals(employee.getId())) {
            throw new IllegalArgumentException("현재 로그인한 관리자 계정은 삭제할 수 없습니다.");
        }

        if (attendanceRecordRepository.existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException("출근 기록이 있는 직원은 삭제할 수 없습니다.");
        }

        employeeRepository.delete(employee);
    }

    private List<Employee> getCompanyEmployees(String employeeCode) {
        Employee admin = getEmployeeByCode(employeeCode);
        return employeeRepository.findAllByCompanyIdOrderByNameAsc(admin.getCompany().getId());
    }

    private Employee getEmployeeByCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Employee getEditableEmployee(String adminEmployeeCode, Long employeeId) {
        Employee admin = getEmployeeByCode(adminEmployeeCode);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다."));

        if (!employee.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new IllegalArgumentException("같은 회사 소속 직원만 관리할 수 있습니다.");
        }

        return employee;
    }

    private void validateDuplicateEmployeeCode(String employeeCode, Long employeeId) {
        String normalizedCode = employeeCode.trim();
        boolean duplicated = employeeId == null
                ? employeeRepository.existsByEmployeeCode(normalizedCode)
                : employeeRepository.existsByEmployeeCodeAndIdNot(normalizedCode, employeeId);

        if (duplicated) {
            throw new IllegalArgumentException("이미 사용 중인 사번입니다.");
        }
    }

    private Map<Long, AttendanceRecord> getTodayRecordsByEmployee(List<Employee> employees) {
        if (employees.isEmpty()) {
            return Map.of();
        }

        Long companyId = employees.get(0).getCompany().getId();
        return attendanceRecordRepository.findAllByEmployeeCompanyIdAndAttendanceDate(companyId, LocalDate.now())
                .stream()
                .collect(Collectors.toMap(record -> record.getEmployee().getId(), Function.identity()));
    }

    private AttendanceState toState(AttendanceRecord record) {
        if (record == null) {
            return AttendanceState.ABSENT;
        }
        if (record.isLate()) {
            return AttendanceState.LATE;
        }
        if (record.getStatus() == AttendanceStatus.CHECKED_OUT) {
            return AttendanceState.CHECKED_OUT;
        }
        return AttendanceState.WORKING;
    }

    private String formatCheckIn(AttendanceRecord record) {
        return record == null ? "-" : record.getCheckInTime().format(TIME_FORMATTER);
    }

    private String formatCheckOut(AttendanceRecord record) {
        return record == null || record.getCheckOutTime() == null
                ? "-"
                : record.getCheckOutTime().format(TIME_FORMATTER);
    }

    private String buildNote(AttendanceRecord record) {
        if (record == null) {
            return "오늘 출근 기록 없음";
        }
        if (record.isLate()) {
            return "지각 출근";
        }
        if (record.getStatus() == AttendanceStatus.CHECKED_OUT) {
            return "퇴근 완료";
        }
        return "근무 중";
    }
}
