package com.leads.sandbox.test.project.address.repository;

import jakarta.persistence.*;

@Entity
@Table(name = "countries_turya")
public class CountryEntity {


    @Id
    @Column
    private Long id;

    @Column(name = "country_name", length = 50)
    private String countryName;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
