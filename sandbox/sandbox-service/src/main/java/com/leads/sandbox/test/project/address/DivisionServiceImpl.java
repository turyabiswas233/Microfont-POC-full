package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveDivision;
import com.leads.sandbox.test.project.address.repository.AddressRepository;
import com.leads.sandbox.test.project.address.service.DivisionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DivisionServiceImpl implements DivisionService {
    private final AddressRepository addressRepository;
    public DivisionServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<RetrieveDivision> findByCountryId(Long countryId) {
        List<Map<String , Object>> rows = addressRepository.getAllDivisionsByCountryId(countryId);
        List<RetrieveDivision> result = new ArrayList<>();
        for (Map<String , Object> row : rows) {
            RetrieveDivision division = new RetrieveDivision();
            division.setId(Long.parseLong(row.get("id").toString()));
            division.setDivisionName(row.get("division_name").toString());
            result.add(division);
        }
        return result;
    }

    @Override
    public RetrieveDivision retrieveDivision(Long divisionId) {
        Map<String , Object> row = addressRepository.getDivisionById(divisionId);
        for(String k: row.keySet()) {
            System.out.println("[DIVISION]"+k + " : " + row.get(k));
        }
        RetrieveDivision division = new RetrieveDivision();
        division.setId(Long.parseLong(row.get("id").toString()));
        division.setDivisionName(row.get("division_name").toString());
        return division;
    }
}
