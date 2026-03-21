package com.attendance.adminweb.domain.entity;

import com.attendance.adminweb.domain.entity.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String employeeCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    protected Employee() {
    }

    public Employee(String employeeCode, String name, String password, EmployeeRole role, Company company) {
        this.employeeCode = employeeCode;
        this.name = name;
        this.password = password;
        this.role = role;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public Company getCompany() {
        return company;
    }

    public void updateProfile(String employeeCode, String name, EmployeeRole role) {
        this.employeeCode = employeeCode;
        this.name = name;
        this.role = role;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
