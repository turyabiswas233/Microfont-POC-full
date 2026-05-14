package com.leads.sandbox.test.project.address.service;

import com.leads.sandbox.test.project.address.query.RetrieveThana;

import java.util.List;

public interface ThanaService {
    List<RetrieveThana> findThanasByDistrictId(Long districtId);

    RetrieveThana retrieveThana(Long thanaId);
}
