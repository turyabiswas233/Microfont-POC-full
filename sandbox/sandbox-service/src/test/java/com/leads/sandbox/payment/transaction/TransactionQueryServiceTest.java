// package com.leads.sandbox.payment.transaction;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.BDDMockito.given;

// // import com.leads.sandbox.payment.transaction.repository.TransactionEntity;
// // import com.leads.sandbox.payment.transaction.repository.TransactionRepository;
// // import com.leads.sandbox.transaction.query.InwardTransaction;
// import java.util.List;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
// class TransactionQueryServiceTest {

//   @Mock
//   private TransactionRepository repository;
  
//   @InjectMocks
//   private TransactionQueryServiceImpl queryService;
  
 
//   private List<TransactionEntity> entities;
  
//   @BeforeEach
//   void setUp(){
//     var entity1 = new TransactionEntity();
//     entity1.setId(1L);
//     entity1.setAmount(5000);
//     entity1.setAccountNumber("111000001");
    
//     var entity2 = new TransactionEntity();
//     entity2.setId(2L);
//     entity2.setAmount(100000);
//     entity2.setAccountNumber("222000001");
//     entities = List.of(entity1, entity2);
    
//   }
  
//   @Test
//   void testRetrieveTransactions() {
//     // Arrange
//     given(repository.findAll()).willReturn(entities);
    
//     // Act
//     List<InwardTransaction> list = queryService.retrieveTransactions();
    
//     // Assert
//     assertThat(list).isNotNull();
//     assertThat(list.size()).isEqualTo(2);
//   }
// }
