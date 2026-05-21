package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveDistrict;
import com.leads.sandbox.test.project.address.repository.AddressRepository;
import com.leads.sandbox.test.project.address.service.DistrictService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class DistrictServiceImpl implements DistrictService {
    private final AddressRepository addressRepository;

    public DistrictServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<RetrieveDistrict> findDistrictByDivisionId(Long divisionId) {
        List<Map<String, Object>> rows = addressRepository.getAllDistrictsByDivisionId(divisionId);
        List<RetrieveDistrict> districts = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RetrieveDistrict retrieveDistrict = new RetrieveDistrict();
            retrieveDistrict.setId(Long.parseLong(row.get("id").toString()));
            retrieveDistrict.setDistrictName(row.get("district_name").toString());
            districts.add(retrieveDistrict);
        }
        return districts;
    }

    @Override
    public RetrieveDistrict retrieveDistrict(Long districtId) {
        Map<String, Object> res = addressRepository.getDistrictById(districtId);
        for(String k: res.keySet()) {
            System.out.println("[DISTRICT]"+k + " : " + res.get(k));
        }
        RetrieveDistrict retrieveDistrict = new RetrieveDistrict();
        retrieveDistrict.setId(Long.parseLong(res.get("id").toString()));
        retrieveDistrict.setDistrictName(res.get("district_name").toString());
        return retrieveDistrict;
    }
}
