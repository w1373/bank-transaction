package com.bank.transaction.service;

import com.bank.transaction.dto.TransactionDto;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.model.Transaction;

import java.util.List;

public interface TransactionService {
    long createTransactionId();
    /**
     * 创建交易
     * @param dto
     * @return
     */
    Transaction createTransaction(TransactionDto dto);

    /**
     * 更新交易
     * @param id
     * @param dto
     * @return
     */
    Transaction updateTransaction(Long id,TransactionDto dto);

    /**
     * 删除交易
     * @param id
     */
    Boolean deleteTransaction(Long id);

    /**
     * 查询单个交易
     * @param id
     * @return
     */
    Transaction getTransaction(Long id);
    /**
     * 查询所有交易
     * @return
     */
    List<Transaction> listTransactions(int page, int size, TransactionType type);
}    