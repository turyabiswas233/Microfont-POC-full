package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveThana;
import com.leads.sandbox.test.project.address.repository.AddressRepository;
import com.leads.sandbox.test.project.address.service.ThanaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ThanaServiceImpl implements ThanaService {

    private final AddressRepository addressRepository;
    public ThanaServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<RetrieveThana> findThanasByDistrictId(Long districtId) {
        List<Map<String, Object>> rows = addressRepository.getAllThanasByDistrictId(districtId);
        List<RetrieveThana> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RetrieveThana retrieveThana = new RetrieveThana();
            retrieveThana.setThanaName(row.get("thana_name").toString());
            retrieveThana.setId(Long.parseLong(row.get("thana_id").toString()));
            result.add(retrieveThana);
        }
        return result;
    }

    @Override
    public RetrieveThana retrieveThana(Long thanaId) {
        Map<String, Object> res = addressRepository.getThanaById(thanaId);
        for(String k: res.keySet()) {
            System.out.println("[THANA]"+k + " : " + res.get(k));
        }
        RetrieveThana retrieveThana = new RetrieveThana();
        retrieveThana.setThanaName(res.get("thana_name").toString());
        retrieveThana.setId(Long.parseLong(res.get("thana_id").toString()));
        return retrieveThana;
    }
}
