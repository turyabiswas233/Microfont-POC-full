package com.leads.sandbox.test.project.address.repository;


import jakarta.persistence.*;

@Entity
@Table(name = "districts_turya")
public class DistrictEntity {

    @Id
    @Column
    private Long id;

    @Column(name = "district_name", length = 50)
    private String districtName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    private DivisionEntity divisionEntity;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public DivisionEntity getDivisionEntity() {
        return divisionEntity;
    }

    public void setDivisionEntity(DivisionEntity divisionEntity) {
        this.divisionEntity = divisionEntity;
    }
}