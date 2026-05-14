package com.leads.sandbox.test.project.address.service;

import com.leads.sandbox.test.project.address.query.RetrieveAddressType;

import java.util.List;

public interface AddressTypeService {
    List<RetrieveAddressType> retrieveFindAll();

    RetrieveAddressType retrieveAddressType(Long addressTypeId);
}
