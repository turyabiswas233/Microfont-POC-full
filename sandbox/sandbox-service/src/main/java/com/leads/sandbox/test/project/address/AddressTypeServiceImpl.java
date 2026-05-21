package com.leads.sandbox.test.project.address;

import com.leads.sandbox.test.project.address.query.RetrieveAddressType;
import com.leads.sandbox.test.project.address.repository.AddressRepository;
import com.leads.sandbox.test.project.address.service.AddressTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AddressTypeServiceImpl implements AddressTypeService {
    private final AddressRepository addressRepository;
    public AddressTypeServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<RetrieveAddressType> retrieveFindAll() {
        List<Map<String, Object>> rows = addressRepository.getAllAddressTypes();
        List<RetrieveAddressType> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RetrieveAddressType retrieveAddressType = new RetrieveAddressType();
            retrieveAddressType.setId(Long.parseLong(row.get("ID").toString()));
            retrieveAddressType.setAddressTypeName((String) row.get("ADDRESS_TYPE_NAME"));
            result.add(retrieveAddressType);
        }
        return result;
    }

    @Override
    public RetrieveAddressType retrieveAddressType(Long addressTypeId) {
        Map<String, Object> res = addressRepository.getAddressTypeById(addressTypeId);
        RetrieveAddressType retrieveAddressType = new RetrieveAddressType();
        retrieveAddressType.setId(Long.parseLong(res.get("ID").toString()));
        retrieveAddressType.setAddressTypeName((String) res.get("ADDRESS_TYPE_NAME"));
        return retrieveAddressType;
    }
}
