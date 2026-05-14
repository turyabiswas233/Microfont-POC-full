package com.leads.sandbox.test.project.address.repository;

import jakarta.persistence.*;

@Entity
@Table(name = "divisions_turya")
public class DivisionEntity {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(name = "division_name", nullable = false, length = 50)
    private String divisionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private CountryEntity countryEntity;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public CountryEntity getCountryEntity() {
        return countryEntity;
    }

    public void setCountry(CountryEntity countryEntity) {
        this.countryEntity = countryEntity;
    }
}
