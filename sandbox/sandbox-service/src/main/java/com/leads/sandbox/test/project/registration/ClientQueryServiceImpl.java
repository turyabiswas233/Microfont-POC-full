package com.leads.sandbox.test.project.registration;

import com.leads.sandbox.test.project.register.query.*;
import com.leads.sandbox.test.project.register.service.ClientQueryService;
import com.leads.sandbox.test.project.registration.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientQueryServiceImpl implements ClientQueryService {
    private final ClientInfoRepo clientInfoRepository;
    private final ClientDetailsRepo clientDetailsRepository;
    private final ClientAddressRepo clientAddressRepository;
    private final ClientAccountInfoRepo clientAccountInfoRepository;

    public ClientQueryServiceImpl(ClientInfoRepo clientInfoRepository,
                                  ClientDetailsRepo clientDetailsRepository,
                                  ClientAddressRepo clientAddressRepository,
                                  ClientAccountInfoRepo clientAccountInfoRepository) {
        this.clientInfoRepository = clientInfoRepository;
        this.clientDetailsRepository = clientDetailsRepository;
        this.clientAddressRepository = clientAddressRepository;
        this.clientAccountInfoRepository = clientAccountInfoRepository;
    }

    private RetrieveClient mapToRetrieveClient(ClientInfoEntity client) {
        RetrieveClient rc = new RetrieveClient();

        RetrieveClientInfo info = new RetrieveClientInfo();
        BeanUtils.copyProperties(client, info);

        rc.setRetrieveClientInfo(info);
        rc.setRetrieveClientDetails(mapDetails(client));
        rc.setRetrieveClientAddress(mapAddress(client));
        rc.setRetrieveClientAccountInfo(mapAccount(client));

        return rc;
    }

    private RetrieveClientDetails mapDetails(ClientInfoEntity client) {
        RetrieveClientDetails dto = new RetrieveClientDetails();

        clientDetailsRepository.findByClientId(client.getClientId()).ifPresent(c -> BeanUtils.copyProperties(c, dto));

        return dto;
    }

    private RetrieveClientAddress mapAddress(ClientInfoEntity client) {
        RetrieveClientAddress dto = new RetrieveClientAddress();

        clientAddressRepository.findByClientId(client.getClientId()).ifPresent(c ->
                BeanUtils.copyProperties(c, dto)
        );

        return dto;
    }

    private RetrieveClientAccountInfo mapAccount(ClientInfoEntity client) {
        RetrieveClientAccountInfo dto = new RetrieveClientAccountInfo();

        clientAccountInfoRepository.findByClientId(client.getClientId()).ifPresent(c -> BeanUtils.copyProperties(c, dto));

        return dto;
    }

    private ClientInfoEntity getClientOrThrow(Long clientId) {
        return clientInfoRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
    }

    @Override
    public RetrieveClient retrieveClient(Long clientId) {
        return mapToRetrieveClient(getClientOrThrow(clientId));
    }

    @Override
    public List<RetrieveClient> retrieveAllClients() {
        return clientInfoRepository.findAll()
                .stream()
                .map(this::mapToRetrieveClient)
                .toList();
    }

    @Override
    public List<RetrieveClientAddressList> retrieveClientsAddresses() {
        return clientInfoRepository.findAll()
                .stream()
                .map(client -> {
                    RetrieveClientAddressList dto = new RetrieveClientAddressList();

                    RetrieveClientInfo info = new RetrieveClientInfo();
                    BeanUtils.copyProperties(client, info);

                    dto.setRetrieveClientInfo(info);
                    dto.setRetrieveClientAddress(mapAddress(client));

                    return dto;
                }).filter(e ->
                        e.getRetrieveClientAddress().getAddressType() != null
                )
                .toList();
    }

    @Override
    public List<RetrieveClientAccountInfoList> retrieveClientsAccountInfoList() {
        return clientInfoRepository.findAll()
                .stream()
                .map(client -> {
                    RetrieveClientAccountInfoList dto = new RetrieveClientAccountInfoList();

                    RetrieveClientInfo info = new RetrieveClientInfo();
                    BeanUtils.copyProperties(client, info);

                    dto.setRetrieveClientInfo(info);
                    dto.setRetrieveClientAccountInfo(mapAccount(client));

                    return dto;
                }).filter(e -> e.getRetrieveClientAccountInfo().getAccountNumber() != null)
                .toList();
    }
}
