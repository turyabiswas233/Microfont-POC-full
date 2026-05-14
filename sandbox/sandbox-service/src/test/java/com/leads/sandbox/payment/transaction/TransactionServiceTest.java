// package com.leads.sandbox.payment.transaction;

// import static org.assertj.core.api.Assertions.assertThat;

// import
// com.leads.sandbox.payment.transaction.repository.TransactionRepository;
// import com.leads.sandbox.transaction.command.RegisterTransaction;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
// class TransactionServiceTest {

// @Mock
// private TransactionRepository repository;

// @InjectMocks
// private TransactionServiceImpl service;

// @BeforeEach
// void setUp() {
// // TODO document why this method is empty
// }

// @Test
// void testRegisterTransaction() throws Exception {
// // Arrange
// var txn = new RegisterTransaction();
// txn.setAmount(5000);
// txn.setAccountNumber("1111111");

// // Act
// var result = service.process(txn);

// // Assert
// assertThat(result.getAccountNumber()).isEqualTo(txn.getAccountNumber());
// }
// }
