package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveCountry;
import com.leads.sandbox.test.project.address.repository.AddressRepository;
import com.leads.sandbox.test.project.address.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CountryServiceImpl implements CountryService {
    private final AddressRepository addressRepository;
    public CountryServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<RetrieveCountry> retrieveFindAll() {
        List<Map<String, Object>> rows = addressRepository.getAllCountries();
        List<RetrieveCountry> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RetrieveCountry retrieveCountry = new RetrieveCountry();
            retrieveCountry.setId(Long.parseLong( row.get("id").toString()));
            retrieveCountry.setCountryName(row.get("country_name").toString());
            result.add(retrieveCountry);
        }
        return result;
    }

    @Override
    public RetrieveCountry retrieveCountry(Long countryId) {
        Map<String, Object> res = addressRepository.getCountryById(countryId);
        for(String k: res.keySet()) {
            System.out.println("[COUNTRY]"+k + " : " + res.get(k));
        }
        RetrieveCountry retrieveCountry = new RetrieveCountry();
        retrieveCountry.setId(Long.parseLong(res.get("id").toString()));
        retrieveCountry.setCountryName(res.get("country_name").toString());
        return retrieveCountry;
    }
}
