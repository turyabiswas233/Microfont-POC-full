package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveDivision;
import com.leads.sandbox.test.project.address.repository.DivisionEntity;
import com.leads.sandbox.test.project.address.repository.DivisionRepository;
import com.leads.sandbox.test.project.address.service.DivisionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DivisionServiceImpl implements DivisionService {
    private final DivisionRepository divisionRepository;

    public DivisionServiceImpl(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    @Override
    public List<RetrieveDivision> findByCountryId(Long countryId) {
        List<DivisionEntity> divisions = divisionRepository.findDivisionEntitiesByCountryEntityId(countryId);
        List<RetrieveDivision> divisionResponses = new ArrayList<>();
        for (DivisionEntity division : divisions) {
            RetrieveDivision divisionResponse = new RetrieveDivision();
            divisionResponse.setDivisionName(division.getDivisionName());
            divisionResponse.setId(division.getId());
            divisionResponses.add(divisionResponse);
        }
        return divisionResponses;
    }

    @Override
    public RetrieveDivision retrieveDivision(Long divisionId) {
        DivisionEntity division = divisionRepository.findById(divisionId).orElse(null);
        if(division == null) {
            return null;
        }
        RetrieveDivision divisionResponse = new RetrieveDivision();
        divisionResponse.setDivisionName(division.getDivisionName());
        divisionResponse.setId(division.getId());
        return divisionResponse;
    }

}
