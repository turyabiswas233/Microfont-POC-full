package com.leads.sandbox.test.project.register.service;

import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.register.query.RetrieveClient;
import com.leads.sandbox.test.project.register.query.RetrieveClientInfo;

import java.util.List;

public interface ClientService {
    RetrieveClientInfo registerClientId(RegisterClientId client);

    List<RetrieveClient> retrieveAllClients();
    RetrieveClient retrieveClient(Long clientId);

    boolean registerFullClient(
            Long clientId,
            RegisterClientDetails registerClientDetails,
            RegisterClientAddress registerClientAddress,
            RegisterClientAccountInfo registerClientAccountInfo);

    void createClientDetails(Long clientId, RegisterClientDetails registerClientDetails);

    void createClientAddress(Long clientId, RegisterClientAddress registerClientAddress);

    void createClientAccountInfo(Long clientId, RegisterClientAccountInfo registerClientAccountInfo);

    void updateClientDetails(Long clientId, RegisterClientDetails registerClientDetails);

    void updateClientAddress(Long clientId, RegisterClientAddress registerClientAddress);

    void updateClientAccountInfo(Long clientId, RegisterClientAccountInfo registerClientAccountInfo);


    boolean deleteFullClient(Long clientId);

    RetrieveClient updateFullClient(Long clientId, RegisterClientDetails registerClientDetails, RegisterClientAddress registerClientAddress, RegisterClientAccountInfo registerClientAccountInfo);
}

