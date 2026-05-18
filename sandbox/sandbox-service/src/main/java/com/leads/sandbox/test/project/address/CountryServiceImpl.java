package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveCountry;
import com.leads.sandbox.test.project.address.repository.CountryEntity;
import com.leads.sandbox.test.project.address.repository.CountryRepository;
import com.leads.sandbox.test.project.address.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {
    private final CountryRepository repository;

    public CountryServiceImpl(CountryRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<RetrieveCountry> retrieveFindAll() {
        List<CountryEntity> countries = repository.findAll();
        List<RetrieveCountry> countryResponses = new ArrayList<>();
        for (CountryEntity country : countries) {
            RetrieveCountry countryResponse = new RetrieveCountry();
            countryResponse.setCountryName(country.getCountryName());
            countryResponse.setId(country.getId());
            countryResponses.add(countryResponse);
        }
        return countryResponses;
    }

    @Override
    public RetrieveCountry retrieveCountry(Long countryId) {
        CountryEntity country =  repository.findById(countryId).orElse(null);
        if(country == null) {
            return null;
        }
        RetrieveCountry countryResponse = new RetrieveCountry();
        countryResponse.setCountryName(country.getCountryName());
        countryResponse.setId(country.getId());
        return countryResponse;
    }
}
