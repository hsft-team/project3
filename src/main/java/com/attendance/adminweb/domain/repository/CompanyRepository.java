package com.attendance.adminweb.domain.repository;

import com.attendance.adminweb.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
