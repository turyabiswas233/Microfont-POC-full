package com.leads.sandbox.test.project.address.repository;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class AddressRepository {

    private static final String PACKAGE_NAME = "PKG_MERG_CLIENT_TURYA";

    private final JdbcTemplate jdbcTemplate;

    public AddressRepository(JdbcTemplate jdbcTemplate) {
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

    public List<Map<String, Object>> getAllAddressTypes() {
        Map<String, Object> result = cursorProcedure("SP_GET_ALL_ADDRESSTYPES").execute();
        return extractCursor(result);
    }

    public Map<String, Object> getAddressTypeById(Long addrId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_addr_id", addrId);
        Map<String, Object> result = cursorProcedure("SP_GET_ADDRESSTYPE_BY_ID").execute(params);
        return extractCursor(result).getFirst();
    }

    public Map<String, Object> getCountryById(Long countryId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_cid", countryId);
        Map<String, Object> result = cursorProcedure("SP_GET_COUNTRY_BY_ID").execute(params);

        return extractCursor(result).getFirst();
    }

    public Map<String, Object> getDivisionById(Long divisionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_did", divisionId);

        Map<String, Object> result = cursorProcedure("SP_GET_DIVISION_BY_ID").execute(params);
        return extractCursor(result).getFirst();
    }

    public Map<String, Object> getDistrictById(Long districtId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_did", districtId);

        Map<String, Object> result =  cursorProcedure("SP_GET_DISTRICT_BY_ID").execute(params);
        return extractCursor(result).getFirst();
    }

    public Map<String, Object> getThanaById(Long thanaId) {
        System.out.println("THANA ID: " + thanaId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_tid", thanaId);

        Map<String, Object> result = cursorProcedure("SP_GET_THANA_BY_ID").execute(params);
        return extractCursor(result).getFirst();
    }

    public List<Map<String, Object>> getAllCountries() {
        Map<String, Object> result = cursorProcedure("SP_GET_ALL_COUNTRIES").execute();
        return extractCursor(result);
    }

    public List<Map<String, Object>> getAllDivisionsByCountryId(Long countryId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_country_id", countryId);

        Map<String, Object> result = cursorProcedure("SP_GET_ALL_DIVISIONS_BY_COUNTRYID").execute(params);
        return extractCursor(result);
    }

    public List<Map<String, Object>> getAllDistrictsByDivisionId(Long divisionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_division_id", divisionId);

        Map<String, Object> result = cursorProcedure("SP_GET_ALL_DISTRICTS_BY_DIVISIONID").execute(params);
        return extractCursor(result);
    }

    public List<Map<String, Object>> getAllThanasByDistrictId(Long districtId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_district_id", districtId);

        Map<String, Object> result = cursorProcedure("SP_GET_ALL_THANAS_BY_DISTRICTID").execute(params);
        return extractCursor(result);
    }
}
