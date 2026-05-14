package com.leads.sandbox.test.project.address.service;

import com.leads.sandbox.test.project.address.query.RetrieveDistrict;

import java.util.List;

public interface DistrictService {
    List<RetrieveDistrict> findDistrictByDivisionId(Long divisionId);

    RetrieveDistrict retrieveDistrict(Long districtId);
}
