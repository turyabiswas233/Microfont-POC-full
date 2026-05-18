package com.leads.sandbox.test.project.register.service;

import com.leads.sandbox.test.project.register.query.RetrieveClient;
import com.leads.sandbox.test.project.register.query.RetrieveClientAccountInfoList;
import com.leads.sandbox.test.project.register.query.RetrieveClientAddressList;

import java.util.List;

public interface ClientQueryService {
    List<RetrieveClientAddressList> retrieveClientsAddresses ();
    List<RetrieveClientAccountInfoList> retrieveClientsAccountInfoList();
    List<RetrieveClient> retrieveAllClients();
    RetrieveClient retrieveClient(Long clientId);
}
