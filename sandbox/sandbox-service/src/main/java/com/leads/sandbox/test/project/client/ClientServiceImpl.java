package com.leads.sandbox.test.project.client;

import com.leads.sandbox.test.project.register.command.*;
import com.leads.sandbox.test.project.client.repository.*;
import org.springframework.stereotype.Service;

import com.leads.sandbox.test.project.register.service.ClientService;

import java.util.Map;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public RegisterClientId registerClientId(RegisterClientId client) {
        Map<String, Object> res = clientRepository.createClient(client.getClientName());
        client.setClientId(Long.parseLong(res.get("P_CLIENT_ID").toString()));
        return client;
    }

    @Override
    public boolean registerFullClient(Long clientId, UpdateClientDetails updateClientDetails, UpdateClientAddress updateClientAddress, UpdateClientAccountInfo updateClientAccountInfo) {
        try {
            System.out.println(updateClientDetails.getDateOfBirth().toString());
            System.out.println(updateClientAccountInfo.getAccountOpenDate().toString());
            Map<String, Object> detailsRow = clientRepository.createClientDetails(
                    clientId, updateClientDetails.getFatherName(), updateClientDetails.getMotherName(), updateClientDetails.getGender(),
                    updateClientDetails.getDateOfBirth(), updateClientDetails.getMaritalStatus(), updateClientDetails.getSpouseName(), updateClientDetails.getNidNumber()
            );


            Map<String, Object> addressRow = clientRepository.createClientAddress(
                    clientId, updateClientAddress.getAddress(), updateClientAddress.getAddressType(), updateClientAddress.getCountry(), updateClientAddress.getDivision(), updateClientAddress.getDistrict(), updateClientAddress.getThana(), updateClientAddress.getCity(), updateClientAddress.getZipCode(), updateClientAddress.getMobileNumber(), updateClientAddress.getEmail()
            );


            Map<String, Object> accountRow = clientRepository.createClientAccount(
                    clientId, updateClientAccountInfo.getOfficeCode(), updateClientAccountInfo.getAccountNumber(), updateClientAccountInfo.getAccountTitle(), updateClientAccountInfo.getAccountOpenDate(), updateClientAccountInfo.getAccountExpiryDate(), updateClientAccountInfo.getLimitAmount()
            );


            return detailsRow.get("P_STATUS").equals("TRUE") && addressRow.get("P_STATUS").equals("TRUE") && accountRow.get("P_STATUS").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean deleteFullClient(Long clientId) {
        boolean status;
        try {
            Map<String, Object> delRes = clientRepository.deleteClient(clientId);
            status = delRes.get("p_status").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = true;
        }
        return status;
    }

    @Override
    public boolean deleteClientAddress(Long clientId) {
        boolean status;
        try {
            Map<String, Object> delRes = clientRepository.deleteClientAddress(clientId);
            status = delRes.get("p_status").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = true;
        }
        return status;
    }

    @Override
    public boolean deleteClientAccountInfo(Long clientId) {
        boolean status;
        try {
            Map<String, Object> delRes = clientRepository.deleteClientAccount(clientId);
            status = delRes.get("p_status").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = true;
        }
        return status;
    }


    @Override
    public boolean updateClientAddress(Long clientId, UpdateClientAddress updateClientAddress) {

        try {
            Map<String, Object> addressRow = clientRepository.updateClientAddress(
                    clientId, updateClientAddress.getAddress(), updateClientAddress.getAddressType(), updateClientAddress.getCountry(), updateClientAddress.getDivision(), updateClientAddress.getDistrict(), updateClientAddress.getThana(), updateClientAddress.getCity(), updateClientAddress.getZipCode(), updateClientAddress.getMobileNumber(), updateClientAddress.getEmail()
            );

            return addressRow.get("P_STATUS").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateClientAccountInfo(Long clientId, UpdateClientAccountInfo updateClientAccountInfo) {
        try {
            Map<String, Object> accountRow = clientRepository.updateClientAccount(
                    clientId, updateClientAccountInfo.getOfficeCode(), updateClientAccountInfo.getAccountNumber(), updateClientAccountInfo.getAccountTitle(), updateClientAccountInfo.getAccountOpenDate(), updateClientAccountInfo.getAccountExpiryDate(), updateClientAccountInfo.getLimitAmount()
            );

            return accountRow.get("P_STATUS").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateFullClient(Long clientId, UpdateClientDetails updateClientDetails, UpdateClientAddress updateClientAddress, UpdateClientAccountInfo updateClientAccountInfo) {
        try {
            System.out.println(updateClientDetails.getDateOfBirth().toString());
            System.out.println(updateClientAccountInfo.getAccountOpenDate().toString());
            Map<String, Object> detailsRow = clientRepository.updateClientDetails(
                    clientId, updateClientDetails.getFatherName(), updateClientDetails.getMotherName(), updateClientDetails.getGender(),
                    updateClientDetails.getDateOfBirth(), updateClientDetails.getMaritalStatus(), updateClientDetails.getSpouseName(), updateClientDetails.getNidNumber()
            );


            Map<String, Object> addressRow = clientRepository.updateClientAddress(
                    clientId, updateClientAddress.getAddress(), updateClientAddress.getAddressType(), updateClientAddress.getCountry(), updateClientAddress.getDivision(), updateClientAddress.getDistrict(), updateClientAddress.getThana(), updateClientAddress.getCity(), updateClientAddress.getZipCode(), updateClientAddress.getMobileNumber(), updateClientAddress.getEmail()
            );


            Map<String, Object> accountRow = clientRepository.updateClientAccount(
                    clientId, updateClientAccountInfo.getOfficeCode(), updateClientAccountInfo.getAccountNumber(), updateClientAccountInfo.getAccountTitle(), updateClientAccountInfo.getAccountOpenDate(), updateClientAccountInfo.getAccountExpiryDate(), updateClientAccountInfo.getLimitAmount()
            );

            return detailsRow.get("P_STATUS").equals("TRUE") && addressRow.get("P_STATUS").equals("TRUE") && accountRow.get("P_STATUS").equals("TRUE");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}