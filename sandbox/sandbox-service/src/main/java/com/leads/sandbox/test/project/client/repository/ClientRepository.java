package com.leads.sandbox.test.project.client.repository;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ClientRepository {

    private static final String PACKAGE_NAME = "PKG_MERG_CLIENT_TURYA";

    private final JdbcTemplate jdbcTemplate;

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private SimpleJdbcCall procedure(String procedureName) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName);
    }

    private SimpleJdbcCall cursorProcedure(String procedureName) {
        return new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName)
                .returningResultSet("p_cursor", new ColumnMapRowMapper());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractCursor(Map<String, Object> result) {
        Object cursor = result.get("p_cursor");
        if (cursor == null) {
            cursor = result.get("P_CURSOR");
        }
        return cursor == null ? Collections.emptyList() : (List<Map<String, Object>>) cursor;
    }

    // CREATE

    public Map<String, Object> createClient(String clientName) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_name", clientName);

        return procedure("SP_CREATE_CLIENT").execute(params);
    }

    public Map<String, Object> createClientDetails(
            Long clientId,
            String fatherName,
            String motherName,
            String gender,
            Date dateOfBirth,
            String maritalStatus,
            String spouseName,
            Long nid
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId)
                .addValue("p_father_name", fatherName)
                .addValue("p_mother_name", motherName)
                .addValue("p_gender", gender)
                .addValue("p_date_of_birth", dateOfBirth)
                .addValue("p_marital_status", maritalStatus)
                .addValue("p_spouse_name", spouseName)
                .addValue("p_nid", nid);

        return procedure("SP_CREATE_CLIENT_DETAILS").execute(params);
    }

    public Map<String, Object> createClientAddress(
            Long clientId,
            String address,
            String addressType,
            String country,
            String division,
            String district,
            String thana,
            String city,
            String zipCode,
            String mobileNumber,
            String email
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_address", address)
                .addValue("p_address_type", addressType)
                .addValue("p_city", city)
                .addValue("p_client_id", clientId)
                .addValue("p_country", country)
                .addValue("p_district", district)
                .addValue("p_division", division)
                .addValue("p_email", email)
                .addValue("p_mobile_number", mobileNumber)
                .addValue("p_thana", thana)
                .addValue("p_zip_code", zipCode);

        return procedure("SP_CREATE_CLIENT_ADDRESS").execute(params);
    }

    public Map<String, Object> createClientAccount(
            Long clientId,
            String officeCode,
            String accountNumber,
            String accountTitle,
            LocalDate accountOpenDate,
            LocalDate accountExpiryDate,
            Long limitAmount
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_account_expiry_date", accountExpiryDate)
                .addValue("p_account_number", accountNumber)
                .addValue("p_account_open_date", accountOpenDate)
                .addValue("p_account_title", accountTitle)
                .addValue("p_client_id", clientId)
                .addValue("p_limit_amount", limitAmount)
                .addValue("p_office_code", officeCode);

        return procedure("SP_CREATE_CLIENT_ACCOUNT").execute(params);
    }


    public List<Map<String, Object>> getAllClients() {
        Map<String, Object> result = cursorProcedure("SP_GET_ALL_CLIENTS").execute();
        return extractCursor(result);
    }

    public List<Map<String, Object>> getAllClientAddresses() {
        Map<String, Object> result = cursorProcedure("SP_GET_ALL_CLIENT_ADDRESSES").execute();
        return extractCursor(result);
    }

    public List<Map<String, Object>> getAllClientAccounts() {
        Map<String, Object> result = cursorProcedure("SP_GET_ALL_CLIENT_ACCOUNTS").execute();
        return extractCursor(result);
    }

    public  List<Map<String, Object>> getClientById(Long clientId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId);

        Map<String, Object> result = cursorProcedure("SP_GET_CLIENT_BY_ID").execute(params);
        return extractCursor(result);
    }

    // UPDATE

    public Map<String, Object> updateClientAddress(
            Long clientId,
            String address,
            String addressType,
            String country,
            String division,
            String district,
            String thana,
            String city,
            String zipCode,
            String mobileNumber,
            String email
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId)
                .addValue("p_address", address)
                .addValue("p_address_type", addressType)
                .addValue("p_city", city)
                .addValue("p_country", country)
                .addValue("p_district", district)
                .addValue("p_division", division)
                .addValue("p_email", email)
                .addValue("p_mobile_number", mobileNumber)
                .addValue("p_thana", thana)
                .addValue("p_zip_code", zipCode);

        return procedure("SP_UPDATE_CLIENT_ADDRESS").execute(params);
    }

    public Map<String, Object> updateClientAccount(
            Long clientId,
            String officeCode,
            String accountNumber,
            String accountTitle,
            LocalDate accountOpenDate,
            LocalDate accountExpiryDate,
            Long limitAmount
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId)
                .addValue("p_account_expiry_date", accountExpiryDate)
                .addValue("p_account_number", accountNumber)
                .addValue("p_account_open_date", accountOpenDate)
                .addValue("p_account_title", accountTitle)
                .addValue("p_limit_amount", limitAmount)
                .addValue("p_office_code", officeCode);

        return procedure("SP_UPDATE_CLIENT_ACCOUNT").execute(params);
    }

    public Map<String, Object> updateClientDetails(
            Long clientId,
            String fatherName,
            String motherName,
            String gender,
            Date dateOfBirth,
            String maritalStatus,
            String spouseName,
            Long nid
    ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId)
                .addValue("p_father_name", fatherName)
                .addValue("p_mother_name", motherName)
                .addValue("p_gender", gender)
                .addValue("p_date_of_birth", dateOfBirth)
                .addValue("p_marital_status", maritalStatus)
                .addValue("p_spouse_name", spouseName)
                .addValue("p_nid", nid);

        return procedure("SP_UPDATE_CLIENT_DETAILS").execute(params);
    }

    // DELETE

    public Map<String, Object> deleteClient(Long clientId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId);

        return procedure("SP_DELETE_CLIENT").execute(params);
    }

    public Map<String, Object> deleteClientAddress(Long clientId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId);

        return procedure("SP_DELETE_CLIENT_ADDRESS").execute(params);
    }

    public Map<String, Object> deleteClientAccount(Long clientId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_client_id", clientId);

        return procedure("SP_DELETE_CLIENT_ACCOUNT").execute(params);
    }
}