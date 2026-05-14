create or replace package payment is

  -- Author  : Momomtajul Karim
  -- Created : 04-Sep-2025 14:24:30
  -- Purpose :
  -- Public function and procedure declarations


      PROCEDURE debit_account(
        p_account_no NUMBER,
        p_amount IN OUT FLOAT
    );

    PROCEDURE update_payment_status(
        p_id IN NUMBER,
        p_state IN NUMBER,
        p_batch_no IN NUMBER,
        p_tracer_no IN NUMBER,
        p_remarks IN VARCHAR2
    );

    PROCEDURE get_transactions(
        p_state NUMBER,
        p_txn_cursor OUT SYS_REFCURSOR
    );

end payment;
/
create or replace package body payment is


      PROCEDURE debit_account(
        p_account_no NUMBER,
        p_amount IN OUT FLOAT
    )IS
    BEGIN
        UPDATE CUSTOMER_ACCOUNT t
        SET t.available_balance = t.available_balance -  p_amount,
            t.ledger_balance  = t.ledger_balance - p_amount
        WHERE t.account_no = p_account_no;

        SELECT t.available_balance
        INTO p_amount
        FROM CUSTOMER_ACCOUNT t
        WHERE t.account_no = p_account_no;

    END debit_account;

    PROCEDURE update_payment_status(
        p_id IN NUMBER,
        p_state IN NUMBER,
        p_batch_no IN NUMBER,
        p_tracer_no IN NUMBER,
        p_remarks IN VARCHAR2
    ) IS
    BEGIN
        UPDATE TRANSACTION_REGISTER t
        SET state       = p_state,
            t.batch_no  = p_batch_no,
            t.tracer_no = p_tracer_no,
            t.remarks   = p_remarks
        WHERE id = p_id;
    END update_payment_status;

    PROCEDURE get_transactions(
        p_state NUMBER,
        p_txn_cursor OUT SYS_REFCURSOR
    ) IS
    BEGIN
        OPEN p_txn_cursor FOR
            select * from TRANSACTION_REGISTER t
            WHERE t.state = p_state;
    END get_transactions;
  -- Initialization
end payment;
/
