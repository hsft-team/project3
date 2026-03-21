package com.attendance.adminweb.domain.repository;

import com.attendance.adminweb.domain.entity.Company;
import com.attendance.adminweb.domain.entity.CompanySetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanySettingRepository extends JpaRepository<CompanySetting, Long> {

    @EntityGraph(attributePaths = "company")
    Optional<CompanySetting> findByCompany(Company company);
}
