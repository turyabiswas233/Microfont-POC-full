package com.leads.sandbox.test.project.address.repository;

import jakarta.persistence.*;

@Entity
@Table(name = "thanas_turya")
public class ThanaEntity
{

    @Id
    @Column(name = "thana_id", nullable = false)
    private Long thanaId;

    @Column(name = "thana_name", length = 50)
    private String thanaName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private DistrictEntity districtEntity;

    // getters & setters

    public Long getThanaId() {
        return thanaId;
    }

    public void setThanaId(Long thanaId) {
        this.thanaId = thanaId;
    }

    public String getThanaName() {
        return thanaName;
    }

    public void setThanaName(String thanaName) {
        this.thanaName = thanaName;
    }

    public DistrictEntity getDistrictEntity() {
        return districtEntity;
    }

    public void setDistrictEntity(DistrictEntity districtEntity) {
        this.districtEntity = districtEntity;
    }
}