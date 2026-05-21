package com.leads.sandbox.test.project.client;

import com.leads.sandbox.test.project.register.query.*;
import com.leads.sandbox.test.project.register.service.ClientQueryService;
import com.leads.sandbox.test.project.client.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClientQueryServiceImpl implements ClientQueryService {
    private final ClientRepository clientRepository;

    public ClientQueryServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<RetrieveClientAddressList> retrieveClientsAddresses() {
        List<Map<String, Object>> rows = clientRepository.getAllClientAddresses();
        List<RetrieveClientAddressList> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RetrieveClientAddressList retrieveClientAddressList = new RetrieveClientAddressList();
            retrieveClientAddressList.setRetrieveClientAddress(mapToRetrieveClientAddress(row));
            retrieveClientAddressList.setRetrieveClientInfo(mapToRetrieveClientInfo(row));
            list.add(retrieveClientAddressList);
        }
        return list;
    }

    @Override
    public List<RetrieveClientAccountInfoList> retrieveClientsAccountInfoList() {
        List<Map<String, Object>> rows = clientRepository.getAllClientAccounts();
        List<RetrieveClientAccountInfoList> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            RetrieveClientAccountInfoList retrieveClientAccountInfoList = new RetrieveClientAccountInfoList();
            retrieveClientAccountInfoList.setRetrieveClientAccountInfo(mapToRetrieveClientAccountInfo(row));
            retrieveClientAccountInfoList.setRetrieveClientInfo(mapToRetrieveClientInfo(row));
            list.add(retrieveClientAccountInfoList);
        }
        return list;
    }

    @Override
    public List<RetrieveClient> retrieveAllClients() {
        try {
            List<Map<String, Object>> rows = clientRepository.getAllClients();
            List<RetrieveClient> list = new ArrayList<>();
            for (Map<String, Object> row : rows) {

                RetrieveClient retrieveClient = new RetrieveClient();
                retrieveClient.setRetrieveClientInfo(mapToRetrieveClientInfo(row));
                retrieveClient.setRetrieveClientDetails(mapToRetrieveClientDetails(row));
                retrieveClient.setRetrieveClientAddress(mapToRetrieveClientAddress(row));
                retrieveClient.setRetrieveClientAccountInfo(mapToRetrieveClientAccountInfo(row));
                list.add(retrieveClient);
            }
            return list;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public RetrieveClient retrieveClient(Long clientId) {
        List<Map<String, Object>> row = clientRepository.getClientById(clientId);
        if (row.isEmpty()) {
            return null;
        }
        return mapToRetrieveClient(row.getFirst());
    }

    // helper methods

    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLong(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value != null ? ((Number) value).longValue() : null;
    }

    private Date getDate(Map<String, Object> row, String key) {

        Object value = row.get(key);
        System.out.println(key + ": " + value);
        if (value != null) {
            return Date.valueOf(value.toString().split(" ")[0]);
        }
        return null;
    }

    private RetrieveClient mapToRetrieveClient(Map<String, Object> row) {
        RetrieveClient client = new RetrieveClient();

        RetrieveClientInfo info = mapToRetrieveClientInfo(row);
        RetrieveClientDetails details = mapToRetrieveClientDetails(row);
        RetrieveClientAddress address = mapToRetrieveClientAddress(row);
        RetrieveClientAccountInfo accountInfo = mapToRetrieveClientAccountInfo(row);

        client.setRetrieveClientInfo(info);
        client.setRetrieveClientDetails(details);
        client.setRetrieveClientAccountInfo(accountInfo);
        client.setRetrieveClientAddress(address);

        return client;
    }

    private RetrieveClientAddress mapToRetrieveClientAddress(Map<String, Object> row) {
        RetrieveClientAddress address = new RetrieveClientAddress();

        address.setAddress(getString(row, "ADDRESS"));
        address.setAddressType(getString(row, "ADDRESS_TYPE"));
        address.setCountry(getString(row, "COUNTRY"));
        address.setDivision(getString(row, "DIVISION"));
        address.setDistrict(getString(row, "DISTRICT"));
        address.setThana(getString(row, "THANA"));
        address.setCity(getString(row, "CITY"));
        address.setZipCode(getString(row, "ZIP_CODE"));
        address.setEmail(getString(row, "EMAIL"));
        address.setMobileNumber(getString(row, "MOBILE_NUMBER"));
        return address;
    }

    private RetrieveClientAccountInfo mapToRetrieveClientAccountInfo(Map<String, Object> row) {
        RetrieveClientAccountInfo accountInfo = new RetrieveClientAccountInfo();
        accountInfo.setAccountTitle(getString(row, "ACCOUNT_TITLE"));
        accountInfo.setOfficeCode(getString(row, "OFFICE_CODE"));
        accountInfo.setAccountNumber(getString(row, "ACCOUNT_NUMBER"));
        accountInfo.setAccountOpenDate(getDate(row, "ACCOUNT_OPEN_DATE"));
        accountInfo.setAccountExpiryDate(getDate(row, "ACCOUNT_EXPIRY_DATE"));
        accountInfo.setLimitAmount(getLong(row, "LIMIT_AMOUNT"));

        return accountInfo;
    }

    private RetrieveClientInfo mapToRetrieveClientInfo(Map<String, Object> row) {
        RetrieveClientInfo info = new RetrieveClientInfo();
        info.setClientId(getLong(row, "CLIENT_ID"));
        info.setClientName(getString(row, "CLIENT_NAME"));
        return info;
    }

    private RetrieveClientDetails mapToRetrieveClientDetails(Map<String, Object> row) {
        RetrieveClientDetails details = new RetrieveClientDetails();
        details.setFatherName(getString(row, "FATHER_NAME"));
        details.setMotherName(getString(row, "MOTHER_NAME"));
        details.setDateOfBirth(getDate(row, "DATE_OF_BIRTH"));
        details.setGender(getString(row, "GENDER"));
        details.setNidNumber(getLong(row, "NID_NUMBER"));
        details.setSpouseName(getString(row, "SPOUSE_NAME"));
        details.setMaritalStatus(getString(row, "MARITAL_STATUS"));

        return details;
    }
}
