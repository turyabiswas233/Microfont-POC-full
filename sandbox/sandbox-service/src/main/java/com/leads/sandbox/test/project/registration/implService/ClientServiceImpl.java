package com.leads.sandbox.test.project.registration.implService;

import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.register.query.*;
import com.leads.sandbox.test.project.registration.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.leads.sandbox.test.project.register.service.ClientService;

import java.util.*;
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
    public RetrieveClientInfo registerClientId(RegisterClientId client) {
        ClientInfoEntity entity = new ClientInfoEntity();
        entity.setClientName(client.getClientName());

        long id = Math.abs(new Random().nextLong() % 123456100);
        entity.setClientId(id);

        clientInfoRepository.save(entity);

        RetrieveClientInfo dto = new RetrieveClientInfo();
        BeanUtils.copyProperties(entity, dto);

        return dto;
    }

    @Transactional
    @Override
    public boolean registerFullClient(Long clientId,
                                      SaveClientDetails details,
                                      SaveClientAddress address,
                                      SaveClientAccountInfo account) {

        createOrUpdateAll(clientId, details, address, account);
        return true;
    }

    @Transactional
    @Override
    public RetrieveClient updateFullClient(Long clientId,
                                           SaveClientDetails details,
                                           SaveClientAddress address,
                                           SaveClientAccountInfo account) {

        ClientInfoEntity client = getClientOrThrow(clientId);

        createOrUpdateAll(clientId, details, address, account);

        return mapToRetrieveClient(client);
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

    private void createOrUpdateAll(Long clientId,
                                   SaveClientDetails details,
                                   SaveClientAddress address,
                                   SaveClientAccountInfo account) {

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
                    Long id = client.getClientId();
                    clientAccountInfoRepository.deleteByClientId(id);
                    clientAddressRepository.deleteByClientId(id);
                    clientDetailsRepository.deleteByClientId(id);
                    clientInfoRepository.delete(client);
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
    public boolean saveClientAddress(Long clientId, SaveClientAddress saveClientAddress) {
        ClientInfoEntity client = getClientOrThrow(clientId);
        try {
            clientAddressRepository.save(buildAddress(client, saveClientAddress));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean saveClientAccountInfo(Long clientId, SaveClientAccountInfo saveClientAccountInfo) {
        ClientInfoEntity client = getClientOrThrow(clientId);
        try {
            clientAccountInfoRepository.save(buildAccount(client, saveClientAccountInfo));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private ClientDetailsEntity buildDetails(ClientInfoEntity client, SaveClientDetails dto) {
        ClientDetailsEntity entity = clientDetailsRepository
                .findByClientId(client.getClientId())
                .orElse(new ClientDetailsEntity());

        BeanUtils.copyProperties(dto, entity);

        entity.setClientId(client.getClientId());
        entity.setClientInfoEntity(client);

        return entity;
    }

    private ClientAddressEntity buildAddress(ClientInfoEntity client, SaveClientAddress dto) {
        ClientAddressEntity entity = clientAddressRepository
                .findByClientId(client.getClientId())
                .orElse(new ClientAddressEntity());

        BeanUtils.copyProperties(dto, entity);

        entity.setClientId(client.getClientId());
        entity.setClientInfoEntity(client);

        return entity;
    }

    private ClientAccountInfoEntity buildAccount(ClientInfoEntity client, SaveClientAccountInfo dto) {
        ClientAccountInfoEntity entity = clientAccountInfoRepository
                .findByClientId(client.getClientId())
                .orElse(new ClientAccountInfoEntity());

        BeanUtils.copyProperties(dto, entity);

        entity.setClientId(client.getClientId());
        entity.setClientInfoEntity(client);

        return entity;
    }
}