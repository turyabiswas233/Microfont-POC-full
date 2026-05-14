package com.leads.sandbox.test.project.address.implService;

import com.leads.sandbox.test.project.address.query.RetrieveAddressType;
import com.leads.sandbox.test.project.address.query.RetrieveCountry;
import com.leads.sandbox.test.project.address.repository.AddressTypeEntity;
import com.leads.sandbox.test.project.address.repository.AddressTypeRepository;
import com.leads.sandbox.test.project.address.repository.CountryEntity;
import com.leads.sandbox.test.project.address.service.AddressTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressTypeServiceImpl implements AddressTypeService {
    private final AddressTypeRepository addressTypeRepository;
    public AddressTypeServiceImpl(AddressTypeRepository addressTypeRepository) {
        this.addressTypeRepository = addressTypeRepository;
    }

    @Override
    public List<RetrieveAddressType> retrieveFindAll() {
        List<AddressTypeEntity> addressTypeEntityList = this.addressTypeRepository.findAll();
        List<RetrieveAddressType> retrieveAddressTypes = new ArrayList<>();
        for (AddressTypeEntity addressTypeEntity : addressTypeEntityList) {
            RetrieveAddressType retrieveAddressType = new RetrieveAddressType();
            retrieveAddressType.setId(addressTypeEntity.getId());
            retrieveAddressType.setAddressTypeName(addressTypeEntity.getAddressTypeName());
            retrieveAddressTypes.add(retrieveAddressType);
        }
        return retrieveAddressTypes;
    }

    @Override
    public RetrieveAddressType retrieveAddressType(Long addressTypeId) {
        AddressTypeEntity addressType =  addressTypeRepository.findById(addressTypeId).orElse(null);
        if(addressType == null) {
            return null;
        }
        RetrieveAddressType retrieveAddressType = new RetrieveAddressType();
        retrieveAddressType.setId(addressType.getId());
        retrieveAddressType.setAddressTypeName(addressType.getAddressTypeName());
        return retrieveAddressType;

    }
}
