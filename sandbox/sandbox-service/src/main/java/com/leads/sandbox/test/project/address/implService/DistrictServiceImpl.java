package com.leads.sandbox.test.project.address.implService;

import com.leads.sandbox.test.project.address.query.RetrieveDistrict;
import com.leads.sandbox.test.project.address.query.RetrieveDivision;
import com.leads.sandbox.test.project.address.repository.DistrictEntity;
import com.leads.sandbox.test.project.address.repository.DistrictRepository;
import com.leads.sandbox.test.project.address.repository.DivisionEntity;
import com.leads.sandbox.test.project.address.service.DistrictService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistrictServiceImpl implements DistrictService {
    private final DistrictRepository districtRepository;

    public DistrictServiceImpl(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }


    @Override
    public List<RetrieveDistrict> findDistrictByDivisionId(Long divisionId) {
        List<DistrictEntity> districts = this.districtRepository.findDistrictsByDivisionEntityId(divisionId);
        List<RetrieveDistrict> districtResponseList = new ArrayList<>();

        for (DistrictEntity district : districts) {
            RetrieveDistrict districtResponse = new RetrieveDistrict();
            districtResponse.setDistrictName(district.getDistrictName());
            districtResponse.setId(district.getDivisionEntity().getId());
            districtResponse.setId(district.getId());
            districtResponseList.add(districtResponse);
        }
        return districtResponseList;
    }

    @Override
    public RetrieveDistrict retrieveDistrict(Long districtId) {
        DistrictEntity district = districtRepository.findById(districtId).orElse(null);
        if(district == null) {
            return null;
        }
        RetrieveDistrict retrieveDistrict = new RetrieveDistrict();
        retrieveDistrict.setDistrictName(district.getDistrictName());
        retrieveDistrict.setId(districtId);
        retrieveDistrict.setId(district.getId());
        return  retrieveDistrict;
    }
}

