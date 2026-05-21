package com.leads.sandbox.test.project.address.service;

import com.leads.sandbox.test.project.address.query.RetrieveDivision;

import java.util.List;

public interface DivisionService {

    List<RetrieveDivision> findByCountryId(Long countryId);

    RetrieveDivision retrieveDivision(Long divisionId);
}
