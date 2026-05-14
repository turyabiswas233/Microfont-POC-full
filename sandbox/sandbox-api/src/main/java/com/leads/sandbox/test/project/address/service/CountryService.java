package com.leads.sandbox.test.project.address.service;

import com.leads.sandbox.test.project.address.query.RetrieveCountry;

import java.util.List;

public interface CountryService {
    List<RetrieveCountry> retrieveFindAll();

    RetrieveCountry retrieveCountry(Long countryId);
}