package com.leads.sandbox.test.project.register.service;

import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.register.query.*;

import java.util.List;

public interface ClientService {
    RetrieveClientInfo registerClientId(RegisterClientId client);

    List<RetrieveClient> retrieveAllClients();
    RetrieveClient retrieveClient(Long clientId);

    List<RetrieveClientAddressList> retrieveClientsAddresses ();

    boolean registerFullClient(
            Long clientId,
            SaveClientDetails saveClientDetails,
            SaveClientAddress saveClientAddress,
            SaveClientAccountInfo saveClientAccountInfo);

    boolean deleteFullClient(Long clientId);
    boolean deleteClientAddress(Long clientId);
    boolean deleteClientAccountInfo(Long clientId);

    boolean saveClientAddress(Long clientId, SaveClientAddress saveClientAddress);
    boolean saveClientAccountInfo(Long clientId, SaveClientAccountInfo saveClientAccountInfo);

    RetrieveClient updateFullClient(Long clientId, SaveClientDetails saveClientDetails, SaveClientAddress saveClientAddress, SaveClientAccountInfo saveClientAccountInfo);

    List<RetrieveClientAccountInfoList> retrieveClientsAccountInfoList();
}

