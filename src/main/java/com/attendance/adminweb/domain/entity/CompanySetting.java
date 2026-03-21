package com.attendance.adminweb.domain.entity;

import com.attendance.adminweb.domain.entity.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "company_settings")
public class CompanySetting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(nullable = false)
    private Integer allowedRadiusMeters;

    @Column(name = "late_after_time", nullable = false)
    private LocalTime lateAfterTime;

    protected CompanySetting() {
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public Integer getAllowedRadiusMeters() {
        return allowedRadiusMeters;
    }

    public LocalTime getLateAfterTime() {
        return lateAfterTime;
    }

    public void updateAllowedRadiusMeters(Integer allowedRadiusMeters) {
        this.allowedRadiusMeters = allowedRadiusMeters;
    }
}
