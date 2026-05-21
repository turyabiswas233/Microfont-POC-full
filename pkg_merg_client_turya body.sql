{\rtf1\ansi\ansicpg1252\cocoartf2870
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 CREATE OR REPLACE PACKAGE BODY pkg_merg_client_turya AS\
\
    --create procedure \
\
    PROCEDURE sp_create_client (\
        p_client_name IN VARCHAR2,\
        p_client_id   OUT LONG\
    ) AS\
    BEGIN\
        IF p_client_name IS NOT NULL THEN\
            p_client_id := trunc(dbms_random.value(0, 12345)) + ( TO_NUMBER ( to_char(systimestamp, 'FF6') ) * 1000 );\
\
            INSERT INTO client_info_turya cit (\
                cit.client_id,\
                cit.client_name,\
                cit.created_at,\
                cit.updated_at\
            ) VALUES ( p_client_id,\
                       p_client_name,\
                       systimestamp,\
                       systimestamp );\
\
            IF SQL%rowcount > 0 THEN\
                dbms_output.put_line('Success');\
            END IF;\
            COMMIT;\
        END IF;\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            dbms_output.put_line('Failed: ' || sqlerrm);\
    END sp_create_client;\
\
    PROCEDURE sp_create_client_details (\
        p_client_id      IN LONG,\
        p_father_name    IN VARCHAR2,\
        p_mother_name    IN VARCHAR2,\
        p_gender         IN VARCHAR2,\
        p_date_of_birth  IN DATE,\
        p_marital_status IN VARCHAR2,\
        p_spouse_name    IN VARCHAR2,\
        p_nid            IN LONG,\
        p_status         OUT VARCHAR2\
    ) AS\
        v_row_count LONG;\
    BEGIN\
        p_status := 'TRUE';\
        SELECT\
            COUNT(1)\
        INTO v_row_count\
        FROM\
            client_info_turya cit;\
\
        INSERT INTO client_details_turya cdt (\
            cdt.client_id,\
            cdt.father_name,\
            cdt.mother_name,\
            cdt.gender,\
            cdt.date_of_birth,\
            cdt.marital_status,\
            cdt.spouse_name,\
            cdt.nid_number,\
            cdt.created_at,\
            cdt.updated_at\
        ) VALUES ( p_client_id,\
                   p_father_name,\
                   p_mother_name,\
                   p_gender,\
                   p_date_of_birth,\
                   p_marital_status,\
                   p_spouse_name,\
                   p_nid,\
                   systimestamp,\
                   systimestamp );\
\
        COMMIT;\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_create_client_details;\
\
    PROCEDURE sp_create_client_address (\
        p_address       IN VARCHAR2,\
        p_address_type  IN VARCHAR2,\
        p_city          IN VARCHAR2,\
        p_client_id     IN LONG,\
        p_country       IN VARCHAR2,\
        p_district      IN VARCHAR2,\
        p_division      IN VARCHAR2,\
        p_email         IN VARCHAR2,\
        p_mobile_number IN VARCHAR2,\
        p_thana         IN VARCHAR2,\
        p_zip_code      IN VARCHAR2,\
        p_status        OUT VARCHAR2\
    ) AS\
    BEGIN\
        INSERT INTO client_addresses_turya (\
            address,\
            address_type,\
            city,\
            client_id,\
            country,\
            created_at,\
            district,\
            division,\
            email,\
            mobile_number,\
            thana,\
            updated_at,\
            zip_code\
        ) VALUES ( p_address,\
                   p_address_type,\
                   p_city,\
                   p_client_id,\
                   p_country,\
                   systimestamp,\
                   p_district,\
                   p_division,\
                   p_email,\
                   p_mobile_number,\
                   p_thana,\
                   systimestamp,\
                   p_zip_code );\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_create_client_address;\
\
    PROCEDURE sp_create_client_account (\
        p_account_expiry_date IN DATE,\
        p_account_number      IN VARCHAR2,\
        p_account_open_date   IN DATE,\
        p_account_title       IN VARCHAR2,\
        p_client_id           IN LONG,\
        p_limit_amount        IN LONG,\
        p_office_code         IN VARCHAR2,\
        p_status              OUT VARCHAR2\
    ) AS\
    BEGIN\
        INSERT INTO client_account_info_turya (\
            account_expiry_date,\
            account_number,\
            account_open_date,\
            account_title,\
            client_id,\
            created_at,\
            limit_amount,\
            office_code,\
            updated_at\
        ) VALUES ( p_account_expiry_date,\
                   p_account_number,\
                   p_account_open_date,\
                   p_account_title,\
                   p_client_id,\
                   systimestamp,\
                   p_limit_amount,\
                   p_office_code,\
                   systimestamp );\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_create_client_account;\
\
    -- get procedure\
\
    PROCEDURE sp_get_all_clients (\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                cit.client_id,\
                                                cit.client_name,\
                                                dt.father_name,\
                                                dt.mother_name,\
                                                dt.date_of_birth,\
                                                dt.gender,\
                                                dt.nid_number,\
                                                dt.spouse_name,\
                                                dt.marital_status,\
                                                addt.address,\
                                                addt.address_type,\
                                                addt.country,\
                                                addt.division,\
                                                addt.district,\
                                                addt.thana,\
                                                addt.city,\
                                                addt.zip_code,\
                                                addt.email,\
                                                addt.mobile_number,\
                                                acct.account_title,\
                                                acct.office_code,\
                                                acct.account_number,\
                                                acct.account_open_date,\
                                                acct.account_expiry_date,\
                                                acct.limit_amount,\
                                                cit.created_at,\
                                                cit.updated_at\
                                            FROM\
                                                client_info_turya         cit\
                                                LEFT JOIN client_details_turya      dt ON cit.client_id = dt.client_id\
                                                LEFT JOIN client_addresses_turya    addt ON cit.client_id = addt.client_id\
                                                LEFT JOIN client_account_info_turya acct ON cit.client_id = acct.client_id\
                          ORDER BY\
                              cit.created_at DESC;\
\
    END sp_get_all_clients;\
\
    PROCEDURE sp_get_client_by_id (\
        p_client_id IN LONG,\
        p_cursor    OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                cit.client_id,\
                                                cit.client_name,\
                                                dt.father_name,\
                                                dt.mother_name,\
                                                dt.date_of_birth,\
                                                dt.gender,\
                                                dt.nid_number,\
                                                dt.spouse_name,\
                                                dt.marital_status,\
                                                addt.address,\
                                                addt.address_type,\
                                                addt.country,\
                                                addt.division,\
                                                addt.district,\
                                                addt.thana,\
                                                addt.city,\
                                                addt.zip_code,\
                                                addt.email,\
                                                addt.mobile_number,\
                                                acct.account_title,\
                                                acct.office_code,\
                                                acct.account_number,\
                                                acct.account_open_date,\
                                                acct.account_expiry_date,\
                                                acct.limit_amount,\
                                                cit.created_at,\
                                                cit.updated_at\
                                            FROM\
                                                client_info_turya         cit\
                                                LEFT JOIN client_details_turya      dt ON cit.client_id = dt.client_id\
                                                LEFT JOIN client_addresses_turya    addt ON cit.client_id = addt.client_id\
                                                LEFT JOIN client_account_info_turya acct ON cit.client_id = acct.client_id\
                          WHERE\
                              cit.client_id = p_client_id\
                          FETCH FIRST 1 ROW ONLY;\
\
    END sp_get_client_by_id;\
\
    PROCEDURE sp_get_all_client_addresses (\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                              *\
                          FROM\
                              client_info_turya      cit\
                              RIGHT JOIN client_addresses_turya cat ON cit.client_id = cat.client_id;\
\
    END sp_get_all_client_addresses;\
\
    PROCEDURE sp_get_all_client_accounts (\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                              *\
                          FROM\
                              client_info_turya         cit\
                              RIGHT JOIN client_account_info_turya cat ON cit.client_id = cat.client_id;\
\
    END sp_get_all_client_accounts;\
\
    PROCEDURE sp_get_all_addresstypes (\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                              *\
                          FROM\
                              address_type_turya;\
\
    END sp_get_all_addresstypes;\
\
    PROCEDURE sp_get_addresstype_by_id (\
        p_addr_id IN LONG,\
        p_cursor  OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                address_type_turya att\
                          WHERE\
                              att.id = p_addr_id;\
\
    END sp_get_addresstype_by_id;\
\
    PROCEDURE sp_get_country_by_id (\
        p_cid    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                countries_turya ct\
                          WHERE\
                              ct.id = p_cid;\
\
    END sp_get_country_by_id;\
\
    PROCEDURE sp_get_division_by_id (\
        p_did    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                divisions_turya dt\
                          WHERE\
                              dt.id = p_did;\
\
    END sp_get_division_by_id;\
\
    PROCEDURE sp_get_district_by_id (\
        p_did    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                districts_turya dt\
                          WHERE\
                              dt.id = p_did;\
\
    END sp_get_district_by_id;\
\
    PROCEDURE sp_get_thana_by_id (\
        p_tid    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                thanas_turya tt\
                          WHERE\
                              tt.thana_id = p_tid;\
\
    END sp_get_thana_by_id;\
\
    PROCEDURE sp_get_all_countries (\
        p_cursor OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                              "A1"."ID"           "ID",\
                              "A1"."COUNTRY_NAME" "COUNTRY_NAME"\
                          FROM\
                              "PLAYGROUND"."COUNTRIES_TURYA" "A1";\
\
    END sp_get_all_countries;\
\
    PROCEDURE sp_get_all_divisions_by_countryid (\
        p_country_id IN LONG,\
        p_cursor     OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                divisions_turya dt\
                          WHERE\
                              dt.country_id = p_country_id;\
\
    END sp_get_all_divisions_by_countryid;\
\
    PROCEDURE sp_get_all_districts_by_divisionid (\
        p_division_id IN LONG,\
        p_cursor      OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                districts_turya dt\
                          WHERE\
                              dt.division_id = p_division_id;\
\
    END sp_get_all_districts_by_divisionid;\
\
    PROCEDURE sp_get_all_thanas_by_districtid (\
        p_district_id IN LONG,\
        p_cursor      OUT SYS_REFCURSOR\
    ) AS\
    BEGIN\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                thanas_turya tt\
                          WHERE\
                              tt.district_id = district_id;\
\
    END sp_get_all_thanas_by_districtid;\
\
    -- update procedure\
\
    PROCEDURE sp_update_client_info (\
        p_client_id   IN LONG,\
        p_client_name IN VARCHAR2,\
        p_cursor      OUT SYS_REFCURSOR,\
        p_status      OUT VARCHAR2\
    ) AS\
    BEGIN\
        UPDATE client_info_turya cit\
        SET\
            cit.client_name = p_client_name\
        WHERE\
            cit.client_id = p_client_id;\
\
        COMMIT;\
        OPEN p_cursor FOR SELECT\
                                                *\
                                            FROM\
                                                client_info_turya cit\
                          WHERE\
                              cit.client_id = p_client_id;\
\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_update_client_info;\
\
    PROCEDURE sp_update_client_details (\
        p_client_id      IN LONG,\
        p_father_name    IN VARCHAR2,\
        p_mother_name    IN VARCHAR2,\
        p_gender         IN VARCHAR2,\
        p_date_of_birth  IN DATE,\
        p_marital_status IN VARCHAR2,\
        p_spouse_name    IN VARCHAR2,\
        p_nid            IN LONG,\
        p_outid          OUT LONG,\
        p_status         OUT VARCHAR2\
    ) AS\
    BEGIN\
        SELECT\
            cit.client_id\
        INTO p_outid\
        FROM\
            client_info_turya cit\
        WHERE\
            cit.client_id = p_client_id;\
\
        IF p_client_id = p_outid THEN\
            UPDATE client_details_turya cdt\
            SET\
                cdt.father_name = p_father_name,\
                cdt.mother_name = p_mother_name,\
                cdt.gender = p_gender,\
                cdt.date_of_birth = p_date_of_birth,\
                cdt.marital_status = p_marital_status,\
                cdt.spouse_name = p_spouse_name,\
                cdt.nid_number = p_nid;\
\
        END IF;\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_update_client_details;\
\
    PROCEDURE sp_update_client_address (\
        p_client_id     IN NUMBER,\
        p_address       IN VARCHAR2,\
        p_address_type  IN VARCHAR2,\
        p_city          IN VARCHAR2,\
        p_country       IN VARCHAR2,\
        p_district      IN VARCHAR2,\
        p_division      IN VARCHAR2,\
        p_email         IN VARCHAR2,\
        p_mobile_number IN VARCHAR2,\
        p_thana         IN VARCHAR2,\
        p_zip_code      IN VARCHAR2,\
        p_status        OUT VARCHAR2\
    ) AS\
    BEGIN\
        UPDATE client_addresses_turya\
        SET\
            address = p_address,\
            address_type = p_address_type,\
            city = p_city,\
            country = p_country,\
            district = p_district,\
            division = p_division,\
            email = p_email,\
            mobile_number = p_mobile_number,\
            thana = p_thana,\
            zip_code = p_zip_code,\
            updated_at = systimestamp\
        WHERE\
            client_id = p_client_id;\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_update_client_address;\
\
    PROCEDURE sp_update_client_account (\
        p_client_id           IN NUMBER,\
        p_account_expiry_date IN DATE,\
        p_account_number      IN VARCHAR2,\
        p_account_open_date   IN DATE,\
        p_account_title       IN VARCHAR2,\
        p_limit_amount        IN NUMBER,\
        p_office_code         IN VARCHAR2,\
        p_status              OUT VARCHAR2\
    ) AS\
    BEGIN\
        UPDATE client_account_info_turya\
        SET\
            account_expiry_date = p_account_expiry_date,\
            account_number = p_account_number,\
            account_open_date = p_account_open_date,\
            account_title = p_account_title,\
            limit_amount = p_limit_amount,\
            office_code = p_office_code,\
            updated_at = systimestamp\
        WHERE\
            client_id = p_client_id;\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'TRUE';\
    END sp_update_client_account;\
\
    -- delete procedure\
\
    PROCEDURE sp_delete_client (\
        p_client_id IN NUMBER,\
        p_status    OUT VARCHAR2\
    ) AS\
    BEGIN\
        -- Delete from child tables first\
\
        SELECT\
            COUNT(1)\
        INTO p_status\
        FROM\
            client_info_turya\
        WHERE\
            client_info_turya.client_id = p_client_id;\
\
        IF p_status = '1' THEN\
            DELETE FROM client_details_turya\
            WHERE\
                client_id = p_client_id;\
\
            DELETE FROM client_addresses_turya\
            WHERE\
                client_id = p_client_id;\
\
            DELETE FROM client_account_info_turya\
            WHERE\
                client_id = p_client_id;\
    \
            -- Finally delete from parent table\
\
            DELETE FROM client_info_turya\
            WHERE\
                client_id = p_client_id;\
\
            COMMIT;\
            p_status := 'TRUE';\
        ELSE\
            p_status := 'FALSE';\
        END IF;\
\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_delete_client;\
\
    PROCEDURE sp_delete_client_address (\
        p_client_id IN NUMBER,\
        p_status    OUT VARCHAR2\
    ) AS\
    BEGIN\
        DELETE FROM client_addresses_turya\
        WHERE\
            client_id = p_client_id;\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_delete_client_address;\
\
    PROCEDURE sp_delete_client_account (\
        p_client_id IN NUMBER,\
        p_status    OUT VARCHAR2\
    ) AS\
    BEGIN\
        DELETE FROM client_account_info_turya\
        WHERE\
            client_id = p_client_id;\
\
        COMMIT;\
        p_status := 'TRUE';\
    EXCEPTION\
        WHEN OTHERS THEN\
            ROLLBACK;\
            p_status := 'FALSE';\
    END sp_delete_client_account;\
\
END pkg_merg_client_turya;}