package com.leads.sandbox.test.project.registration.implService;

import com.leads.sandbox.test.project.address.repository.*;
import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.register.query.*;
import com.leads.sandbox.test.project.registration.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.leads.sandbox.test.project.register.service.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientInfoRepo clientInfoRepository;
    private final ClientDetailsRepo clientDetailsRepository;
    private final ClientAddressRepo clientAddressRepository;
    private final ClientAccountInfoRepo clientAccountInfoRepository;

    public ClientServiceImpl(ClientInfoRepo clientInfoRepository, ClientDetailsRepo clientDetailsRepository, ClientAddressRepo clientAddressRepository, ClientAccountInfoRepo clientAccountInfoRepository
    ) {
        this.clientInfoRepository = clientInfoRepository;
        this.clientDetailsRepository = clientDetailsRepository;
        this.clientAddressRepository = clientAddressRepository;
        this.clientAccountInfoRepository = clientAccountInfoRepository;
    }

    @Override
    public RetrieveClient retrieveClient(Long clientId) {
        ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow();
        return getRetrieveClient(clientInfoEntity);
    }

    @NonNull
    private RetrieveClient getRetrieveClient(ClientInfoEntity clientInfoEntity) {
        RetrieveClient rc = new RetrieveClient();
        RetrieveClientInfo retrieveClientInfo = new RetrieveClientInfo();
        BeanUtils.copyProperties(clientInfoEntity, retrieveClientInfo);

        RetrieveClientDetails retrieveClientDetails = retrieveClientDetails(clientInfoEntity);
        RetrieveClientAccountInfo retrieveClientAccountInfo = retrieveClientAccountInfo(clientInfoEntity);
        RetrieveClientAddress retrieveClientAddress = retrieveClientAddress(clientInfoEntity);

        rc.setRetrieveClientInfo(retrieveClientInfo);
        rc.setRetrieveClientAccountInfo(retrieveClientAccountInfo);
        rc.setRetrieveClientAddress(retrieveClientAddress);
        rc.setRetrieveClientDetails(retrieveClientDetails);
        return rc;
    }

    @Override
    public List<RetrieveClient> retrieveAllClients() {
        List<ClientInfoEntity> clientInfoEntityList = clientInfoRepository.findAll();
        List<RetrieveClient> retrieveAllClients = new ArrayList<>();

        for (ClientInfoEntity clientInfoEntity : clientInfoEntityList) {
            RetrieveClient rc = getRetrieveClient(clientInfoEntity);
            retrieveAllClients.add(rc);
        }
        return retrieveAllClients;
    }

    @NotNull
    private RetrieveClientDetails retrieveClientDetails(ClientInfoEntity client) {
        RetrieveClientDetails rci = new RetrieveClientDetails();
        Optional<ClientDetailsEntity> clientDetailsOptional = clientDetailsRepository.findByClientId(client.getClientId()).stream().findFirst();
        clientDetailsOptional.ifPresent(clientDetails -> {
            rci.setFatherName(clientDetailsOptional.get().getFatherName());
            rci.setMotherName(clientDetailsOptional.get().getMotherName());
            rci.setDateOfBirth(clientDetailsOptional.get().getDateOfBirth());
            rci.setGender(clientDetailsOptional.get().getGender());
            rci.setMaritalStatus(clientDetailsOptional.get().getMaritalStatus());
            rci.setSpouseName(clientDetailsOptional.get().getSpouseName());
            rci.setDateOfBirth(clientDetailsOptional.get().getDateOfBirth());
            rci.setNidNumber(clientDetailsOptional.get().getNidNumber());
        });
        return rci;
    }

    @NotNull
    private RetrieveClientAccountInfo retrieveClientAccountInfo(ClientInfoEntity client) {
        RetrieveClientAccountInfo rcai = new RetrieveClientAccountInfo();
        Optional<ClientAccountInfoEntity> clientAccountInfo = clientAccountInfoRepository.findByClientId(client.getClientId()).stream().findFirst();
        clientAccountInfo.ifPresent(cai -> {

            rcai.setAccountNumber(String.valueOf(cai.getAccountNumber()));
            rcai.setAccountTitle(cai.getAccountTitle());
            rcai.setAccountOpenDate(cai.getAccountOpenDate());
            rcai.setAccountExpiryDate(cai.getAccountExpiryDate());
            rcai.setLimitAmount(cai.getLimitAmount());
            rcai.setOfficeCode(cai.getOfficeCode());
        });
        return rcai;
    }

    @NotNull
    private RetrieveClientAddress retrieveClientAddress(ClientInfoEntity client) {
        RetrieveClientAddress rca = new RetrieveClientAddress();
        Optional<ClientAddressEntity> clientAddress = clientAddressRepository.findByClientId(client.getClientId()).stream().findFirst();

        clientAddress.ifPresent(ca -> {
            rca.setAddressType(ca.getAddressType());
            rca.setCity(ca.getCity());
            rca.setCountry(ca.getCountry());
            rca.setDivision(ca.getDivision());
            rca.setDistrict(ca.getDistrict());
            rca.setThana(String.valueOf(ca.getThana()));
            rca.setEmail(ca.getEmail());
            rca.setZipCode(ca.getZipCode());
            rca.setMobileNumber(ca.getMobileNumber());
            rca.setAddress(ca.getAddress());
        });
        return rca;
    }


    @Override
    public RetrieveClientInfo registerClientId(RegisterClientId client) {
        ClientInfoEntity clientUser = new ClientInfoEntity();
        clientUser.setClientName(client.getClientName());

        // simple clientId generation logic
        clientUser.setClientId(System.currentTimeMillis());
        long randomId = Math.abs((new Random()).nextLong() % 123456100);
        clientUser.setClientId(randomId);
        clientInfoRepository.save(clientUser);
        RetrieveClientInfo rci = new RetrieveClientInfo();
        BeanUtils.copyProperties(clientUser, rci);
        return rci;
    }

    @Transactional
    @Override
    public RetrieveClient updateFullClient(Long clientId, RegisterClientDetails registerClientDetails, RegisterClientAddress registerClientAddress, RegisterClientAccountInfo registerClientAccountInfo) {
        ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).stream().findFirst().orElse(null);
        try {
            if (clientInfoEntity == null) {
                throw new Error("Failed to update client with id " + clientId);
            }
            this.updateClientDetails(clientInfoEntity.getClientId(), registerClientDetails);
            this.updateClientAddress(clientInfoEntity.getClientId(), registerClientAddress);
            this.updateClientAccountInfo(clientInfoEntity.getClientId(), registerClientAccountInfo);

            RetrieveClient rci = new RetrieveClient();
            RetrieveClientInfo rci2 = new RetrieveClientInfo();
            RetrieveClientDetails rci3 = new RetrieveClientDetails();
            RetrieveClientAddress rci4 = new RetrieveClientAddress();
            RetrieveClientAccountInfo rci5 = new RetrieveClientAccountInfo();

            BeanUtils.copyProperties(clientInfoEntity, rci2);
            BeanUtils.copyProperties(registerClientDetails, rci3);
            BeanUtils.copyProperties(registerClientAddress, rci4);
            BeanUtils.copyProperties(registerClientAccountInfo, rci5);

            rci.setRetrieveClientInfo(rci2);
            rci.setRetrieveClientDetails(rci3);
            rci.setRetrieveClientAddress(rci4);
            rci.setRetrieveClientAccountInfo(rci5);

            return rci;

        } catch (Exception e) {
            throw new Error("Failed to update client with id " + clientId, e);
        }
    }

    @Transactional
    @Override
    public boolean registerFullClient(Long clientId, RegisterClientDetails registerClientDetails, RegisterClientAddress registerClientAddress, RegisterClientAccountInfo registerClientAccountInfo) {

        boolean result = false;

        try {
            createClientDetails(clientId, registerClientDetails);
            createClientAddress(clientId, registerClientAddress);
            createClientAccountInfo(clientId, registerClientAccountInfo);

            result = true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }


    @Override
    public void createClientDetails(Long clientId, RegisterClientDetails saveClientDetails) {
        try {

            ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));


            ClientDetailsEntity clientDetails = getClientDetails(clientInfoEntity, saveClientDetails);

            clientDetailsRepository.save(clientDetails);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void createClientAddress(Long clientId, RegisterClientAddress registerClientAddress) {
        try {
            ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));
            ClientAddressEntity clientAddressEntity = getClientAddress(clientInfoEntity, registerClientAddress);

            clientAddressRepository.save(clientAddressEntity);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void createClientAccountInfo(Long clientId, RegisterClientAccountInfo saveRegisterClientAccountInfo) {

        try {
            ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));
            ClientAccountInfoEntity clientAccountInfoEntity = getClientAccountInfo(clientInfoEntity, saveRegisterClientAccountInfo);
            clientAccountInfoRepository.save(clientAccountInfoEntity);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updateClientDetails(Long clientId, RegisterClientDetails registerClientDetails) {
        try {
            ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));
            ClientDetailsEntity clientDetailsEntity = getClientDetails(clientInfoEntity, registerClientDetails);
            clientDetailsRepository.save(clientDetailsEntity);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updateClientAddress(Long clientId, RegisterClientAddress registerClientAddress) {
        try {
            ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));
            ClientAddressEntity clientAddressEntity = getClientAddress(clientInfoEntity, registerClientAddress);
            clientAddressRepository.save(clientAddressEntity);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updateClientAccountInfo(Long clientId, RegisterClientAccountInfo registerClientAccountInfo) {
        try {
            ClientInfoEntity clientInfoEntity = clientInfoRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Client not found"));
            ClientAccountInfoEntity clientAccountInfoEntity = getClientAccountInfo(clientInfoEntity, registerClientAccountInfo);
            clientAccountInfoRepository.save(clientAccountInfoEntity);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteFullClient(Long clientId) {
        try {
            Optional<ClientInfoEntity> cie = clientInfoRepository.findByClientId(clientId);
            if (cie.isPresent()) {
                ClientInfoEntity clientInfoEntity = cie.get();
                clientAccountInfoRepository.deleteByClientId(clientInfoEntity.getClientId());
                clientAddressRepository.deleteByClientId(clientInfoEntity.getClientId());
                clientDetailsRepository.deleteByClientId(clientInfoEntity.getClientId());
                clientInfoRepository.delete(clientInfoEntity);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }


    @NonNull
    private ClientDetailsEntity getClientDetails(ClientInfoEntity clientInfoEntity, RegisterClientDetails saveClientDetails) {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        if (clientDetailsRepository.findByClientId(clientInfoEntity.getClientId()).isPresent()) {
            clientDetails = clientDetailsRepository.findByClientId(clientInfoEntity.getClientId()).orElseThrow(() -> new IllegalArgumentException("Client not found"));
        }
        clientDetails.setClientId(clientInfoEntity.getClientId());
        clientDetails.setFatherName(saveClientDetails.getFatherName());
        clientDetails.setMotherName(saveClientDetails.getMotherName());
        clientDetails.setGender(saveClientDetails.getGender());
        clientDetails.setSpouseName(!saveClientDetails.getSpouseName().isEmpty() ? saveClientDetails.getSpouseName() : "");
        clientDetails.setDateOfBirth(saveClientDetails.getDateOfBirth());
        clientDetails.setMaritalStatus(saveClientDetails.getMaritalStatus());
        clientDetails.setNidNumber(saveClientDetails.getNidNumber());
        clientDetails.setClientInfoEntity(clientInfoEntity);
        return clientDetails;
    }

    @NonNull
    private ClientAddressEntity getClientAddress(ClientInfoEntity clientInfoEntity, RegisterClientAddress registerClientAddress) {
        ClientAddressEntity clientAddressEntity = new ClientAddressEntity();
        if (clientAddressRepository.findByClientId(clientInfoEntity.getClientId()).isPresent()) {
            clientAddressEntity = clientAddressRepository.findByClientId(clientInfoEntity.getClientId()).get();
        }
        clientAddressEntity.setAddressType(registerClientAddress.getAddressType());
        clientAddressEntity.setClientId(clientInfoEntity.getClientId());
        clientAddressEntity.setAddress(registerClientAddress.getAddress());
        clientAddressEntity.setCity(registerClientAddress.getCity());
        clientAddressEntity.setCountry(registerClientAddress.getCountry());
        clientAddressEntity.setDivision(registerClientAddress.getDivision());
        clientAddressEntity.setDistrict(registerClientAddress.getDistrict());
        clientAddressEntity.setThana(registerClientAddress.getThana());
        clientAddressEntity.setMobileNumber(registerClientAddress.getMobileNumber());
        clientAddressEntity.setEmail(registerClientAddress.getEmail());
        clientAddressEntity.setZipCode(registerClientAddress.getZipCode());
        clientAddressEntity.setClientInfoEntity(clientInfoEntity);

        return clientAddressEntity;
    }

    @NonNull
    private ClientAccountInfoEntity getClientAccountInfo(ClientInfoEntity clientInfoEntity, RegisterClientAccountInfo accountInfo) {
        ClientAccountInfoEntity clientAccountInfoEntity = new ClientAccountInfoEntity();
        if (clientAccountInfoRepository.findByClientId(clientInfoEntity.getClientId()).isPresent()) {
            clientAccountInfoEntity = clientAccountInfoRepository.findByClientId(clientInfoEntity.getClientId()).get();
        }
        clientAccountInfoEntity.setClientId(clientInfoEntity.getClientId());
        clientAccountInfoEntity.setOfficeCode(accountInfo.getOfficeCode());
        clientAccountInfoEntity.setAccountNumber(accountInfo.getAccountNumber());
        clientAccountInfoEntity.setAccountTitle(accountInfo.getAccountTitle());
        clientAccountInfoEntity.setAccountOpenDate(accountInfo.getAccountOpenDate());
        clientAccountInfoEntity.setAccountExpiryDate(accountInfo.getAccountExpiryDate());
        clientAccountInfoEntity.setLimitAmount(accountInfo.getLimitAmount());
        clientAccountInfoEntity.setClientInfoEntity(clientInfoEntity);

        return clientAccountInfoEntity;
    }
}