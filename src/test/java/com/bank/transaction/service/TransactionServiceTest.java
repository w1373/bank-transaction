package com.bank.transaction.service;

import com.bank.transaction.dto.TransactionDto;
import com.bank.transaction.enums.ExceptionType;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.exception.BusinessException;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @InjectMocks
    private TransactionServiceImpl transactionService;


    @BeforeEach
    void setUp() {

    }

    @Test
    void createTransaction() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountNumber("ACC123");
        transactionDto.setAmount(BigDecimal.valueOf(100.00));
        transactionDto.setType(TransactionType.DEPOSIT);
        transactionDto.setDescription("测试数据");
        transactionDto.setId(1000L);

        Transaction createdTransaction = transactionService.createTransaction(transactionDto);

        assertNotNull(createdTransaction);
        assertNotNull(createdTransaction.getId());

        assertEquals(
                assertThrows(BusinessException.class, () -> transactionService.createTransaction(transactionDto)).getCode(),
                ExceptionType.TRANSACTION_DUPLICATE.getCode());
    }

    @Test
    void updateTransaction_ShouldUpdateFields() {
        TransactionDto dto = new TransactionDto();
        dto.setId(1001L);
        dto.setType(TransactionType.DEPOSIT);
        dto.setAmount(BigDecimal.valueOf(500));
        transactionService.createTransaction(dto);
    
        TransactionDto updateDto = new TransactionDto();
        updateDto.setDescription("更新描述");
        updateDto.setAmount(BigDecimal.valueOf(800));
        updateDto.setType(TransactionType.TRANSFER);
        
        Transaction updated = transactionService.updateTransaction(1001L, updateDto);
    
        assertEquals("更新描述", updated.getDescription());
        assertEquals(0, BigDecimal.valueOf(800).compareTo(updated.getAmount()));
        assertEquals(TransactionType.TRANSFER, updated.getType());
    }
    
    @Test
    void updateNonExistingTransaction_ShouldThrowException() {
        TransactionDto updateDto = new TransactionDto();
        BusinessException exception = assertThrows(BusinessException.class,
            () -> transactionService.updateTransaction(9999L, updateDto));
        
        assertEquals(ExceptionType.TRANSACTION_NOT_EXIST.getCode(), exception.getCode());
        assertTrue(exception.getMessage().contains("9999"));
    }
    
    @Test
    void deleteTransaction_ShouldRemoveFromSystem() {
        TransactionDto dto = new TransactionDto();
        dto.setId(1002L);
        transactionService.createTransaction(dto);
    
        assertTrue(transactionService.deleteTransaction(1002L));
        assertEquals(assertThrows(BusinessException.class, () -> transactionService.deleteTransaction(1002L)).getCode(),ExceptionType.TRANSACTION_NOT_EXIST.getCode());
    }
    
    @Test
    void listTransactions_ShouldReturnPagedResults() {
        IntStream.range(0, 5).forEach(i -> {
            TransactionDto dto = new TransactionDto();
            dto.setId(1003L + i);
            transactionService.createTransaction(dto);
        });
    
        List<Transaction> result = transactionService.listTransactions(0, 3, null);
        assertEquals(3, result.size());
    }
    
    @Test
    void getTransaction_ShouldReturnCorrectEntity() {
        TransactionDto dto = new TransactionDto();
        dto.setId(1004L);
        Transaction created = transactionService.createTransaction(dto);
    
        Transaction found = transactionService.getTransaction(1004L);
        assertEquals(created.getId(), found.getId());
    }
}