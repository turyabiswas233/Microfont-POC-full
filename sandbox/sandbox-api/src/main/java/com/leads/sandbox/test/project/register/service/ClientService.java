package com.leads.sandbox.test.project.register.service;

import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.register.query.*;

public interface ClientService {
    RegisterClientId registerClientId(RegisterClientId client);

    boolean registerFullClient(
            Long clientId,
            UpdateClientDetails updateClientDetails,
            UpdateClientAddress updateClientAddress,
            UpdateClientAccountInfo saveClientAccountInfo);

    boolean deleteFullClient(Long clientId);
    boolean deleteClientAddress(Long clientId);
    boolean deleteClientAccountInfo(Long clientId);

    boolean updateClientAddress(Long clientId, UpdateClientAddress updateClientAddress);
    boolean updateClientAccountInfo(Long clientId, UpdateClientAccountInfo saveClientAccountInfo);
    RetrieveClient updateFullClient(Long clientId, UpdateClientDetails updateClientDetails, UpdateClientAddress updateClientAddress, UpdateClientAccountInfo saveClientAccountInfo);

}

