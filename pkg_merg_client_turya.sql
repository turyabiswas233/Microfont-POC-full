{\rtf1\ansi\ansicpg1252\cocoartf2870
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 CREATE OR REPLACE PACKAGE pkg_merg_client_turya AS\
    PROCEDURE sp_create_client (\
        p_client_name IN VARCHAR2,\
        p_client_id   OUT LONG\
    );\
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
    );\
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
    );\
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
    );\
\
    PROCEDURE sp_get_all_clients (\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_client_by_id (\
        p_client_id IN LONG,\
        p_cursor    OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_client_addresses (\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_client_accounts (\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_addresstypes (\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_addresstype_by_id (\
        p_addr_id IN LONG,\
        p_cursor  OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_country_by_id (\
        p_cid    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_division_by_id (\
        p_did    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_district_by_id (\
        p_did    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_thana_by_id (\
        p_tid    IN LONG,\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_countries (\
        p_cursor OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_divisions_by_countryid (\
        p_country_id IN LONG,\
        p_cursor     OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_districts_by_divisionid (\
        p_division_id IN LONG,\
        p_cursor      OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_get_all_thanas_by_districtid (\
        p_district_id IN LONG,\
        p_cursor      OUT SYS_REFCURSOR\
    );\
\
    PROCEDURE sp_update_client_info (\
        p_client_id   IN LONG,\
        p_client_name IN VARCHAR2,\
        p_cursor      OUT SYS_REFCURSOR,\
        p_status      OUT VARCHAR2\
    );\
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
    );\
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
    );\
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
    );\
\
    PROCEDURE sp_delete_client (\
        p_client_id IN NUMBER,\
        p_status    OUT VARCHAR2\
    );\
\
    PROCEDURE sp_delete_client_address (\
        p_client_id IN NUMBER,\
        p_status    OUT VARCHAR2\
    );\
\
    PROCEDURE sp_delete_client_account (\
        p_client_id IN NUMBER,\
        p_status    OUT VARCHAR2\
    );\
\
END pkg_merg_client_turya;}