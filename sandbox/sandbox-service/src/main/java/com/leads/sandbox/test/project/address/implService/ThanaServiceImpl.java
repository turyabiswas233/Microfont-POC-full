package com.leads.sandbox.test.project.address.implService;

import com.leads.sandbox.test.project.address.query.RetrieveDivision;
import com.leads.sandbox.test.project.address.query.RetrieveThana;
import com.leads.sandbox.test.project.address.repository.DivisionEntity;
import com.leads.sandbox.test.project.address.repository.ThanaEntity;
import com.leads.sandbox.test.project.address.repository.ThanaRepository;
import com.leads.sandbox.test.project.address.service.ThanaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThanaServiceImpl implements ThanaService {

    private final ThanaRepository repository;

    public ThanaServiceImpl(ThanaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RetrieveThana> findThanasByDistrictId(Long districtId) {
        List<ThanaEntity> thanas = repository.findThanaEntitiesByDistrictEntityId(districtId);
        List<RetrieveThana> responses = new ArrayList<>();
        for (ThanaEntity thana : thanas) {
            RetrieveThana response = new RetrieveThana();
            response.setId(thana.getThanaId());
            response.setThanaName(thana.getThanaName());
            responses.add(response);
        }
        return responses;
    }

    @Override
    public RetrieveThana retrieveThana(Long thanaId) {
        ThanaEntity thana = repository.findById(thanaId).orElse(null);
        if (thana == null) {
            return null;
        }
        RetrieveThana retrieveThana = new RetrieveThana();
        retrieveThana.setId(thanaId);
        retrieveThana.setThanaName(thana.getThanaName());

        return retrieveThana;
    }
}
