package com.leads.sandbox.test.project.registration;

import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.register.query.*;
import com.leads.sandbox.test.project.registration.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.leads.sandbox.test.project.register.service.ClientService;

import java.sql.Timestamp;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientInfoRepo clientInfoRepository;
    private final ClientDetailsRepo clientDetailsRepository;
    private final ClientAddressRepo clientAddressRepository;
    private final ClientAccountInfoRepo clientAccountInfoRepository;

    public ClientServiceImpl(ClientInfoRepo clientInfoRepository,
                             ClientDetailsRepo clientDetailsRepository,
                             ClientAddressRepo clientAddressRepository,
                             ClientAccountInfoRepo clientAccountInfoRepository) {
        this.clientInfoRepository = clientInfoRepository;
        this.clientDetailsRepository = clientDetailsRepository;
        this.clientAddressRepository = clientAddressRepository;
        this.clientAccountInfoRepository = clientAccountInfoRepository;
    }

    private ClientInfoEntity getClientOrThrow(Long clientId) {
        return clientInfoRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
    }

    private boolean deleteClientData(Long clientId, Consumer<Long> deleteAction) {
        System.out.println("Delete client data: " + clientId);
        return clientInfoRepository.findByClientId(clientId)
                .map(client -> {
                    deleteAction.accept(client.getClientId());
                    System.out.println("Delete client data: " + client.getClientId());
                    return true;
                })
                .orElse(false);
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

    @Override
    public RegisterClientId registerClientId(RegisterClientId client) {
        ClientInfoEntity clientInfo = new ClientInfoEntity();
        clientInfo.setClientName(client.getClientName());
        Random random = new Random();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        clientInfo.setClientId(random.nextLong(12345) + timestamp.getNanos());
        clientInfoRepository.save(clientInfo);
        client.setClientId(clientInfo.getClientId());
        return  client;
    }
    @Transactional
    @Override
    public boolean registerFullClient(Long clientId,
                                      UpdateClientDetails details,
                                      UpdateClientAddress address,
                                      UpdateClientAccountInfo account) {

        createOrUpdateAll(clientId, details, address, account);
        return true;
    }

    @Transactional
    @Override
    public RetrieveClient updateFullClient(Long clientId,
                                           UpdateClientDetails details,
                                           UpdateClientAddress address,
                                           UpdateClientAccountInfo account) {

        ClientInfoEntity client = getClientOrThrow(clientId);

        createOrUpdateAll(clientId, details, address, account);

        return mapToRetrieveClient(client);
    }

    private void createOrUpdateAll(Long clientId,
                                   UpdateClientDetails details,
                                   UpdateClientAddress address,
                                   UpdateClientAccountInfo account) {

        ClientInfoEntity client = getClientOrThrow(clientId);

        clientDetailsRepository.save(buildDetails(client, details));
        clientAddressRepository.save(buildAddress(client, address));
        clientAccountInfoRepository.save(buildAccount(client, account));
    }


    @Override
    @Transactional
    public boolean deleteFullClient(Long clientId) {
        return clientInfoRepository.findByClientId(clientId)
                .map(client -> {
                    Long id = client.getId();
                    clientInfoRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean deleteClientAddress(Long clientId) {
        return deleteClientData(clientId, clientAddressRepository::deleteByClientId);
    }

    @Override
    public boolean deleteClientAccountInfo(Long clientId) {
        return deleteClientData(clientId, clientAccountInfoRepository::deleteByClientId);
    }

    @Override
    public boolean updateClientAddress(Long clientId, UpdateClientAddress updateClientAddress) {
        ClientInfoEntity client = getClientOrThrow(clientId);
        try {
            clientAddressRepository.save(buildAddress(client, updateClientAddress));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateClientAccountInfo(Long clientId, UpdateClientAccountInfo updateClientAccountInfo) {
        ClientInfoEntity client = getClientOrThrow(clientId);
        try {
            clientAccountInfoRepository.save(buildAccount(client, updateClientAccountInfo));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private ClientDetailsEntity buildDetails(ClientInfoEntity client, UpdateClientDetails dto) {
        ClientDetailsEntity entity = clientDetailsRepository
                .findByClientId(client.getClientId())
                .orElse(new ClientDetailsEntity());

        BeanUtils.copyProperties(dto, entity);

        entity.setClientId(client.getClientId());
        entity.setClientInfoEntity(client);

        return entity;
    }

    private ClientAddressEntity buildAddress(ClientInfoEntity client, UpdateClientAddress dto) {
        ClientAddressEntity entity = clientAddressRepository
                .findByClientId(client.getClientId())
                .orElse(new ClientAddressEntity());

        BeanUtils.copyProperties(dto, entity);

        entity.setClientId(client.getClientId());
        entity.setClientInfoEntity(client);

        return entity;
    }

    private ClientAccountInfoEntity buildAccount(ClientInfoEntity client, UpdateClientAccountInfo dto) {
        ClientAccountInfoEntity entity = clientAccountInfoRepository
                .findByClientId(client.getClientId())
                .orElse(new ClientAccountInfoEntity());

        BeanUtils.copyProperties(dto, entity);

        entity.setClientId(client.getClientId());
        entity.setClientInfoEntity(client);

        return entity;
    }
}