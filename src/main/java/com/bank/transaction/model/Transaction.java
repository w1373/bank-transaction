package com.bank.transaction.model;

import com.bank.transaction.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private final Long id;
    /**
     * 账号
     */
    private String accountNumber;

    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 类型
     */
    private TransactionType type;
    /**
     * 创建时间
     */
    private LocalDateTime timestamp;

    /**
     * 描述
     */
    private String description;

    public Transaction(Long id) {
        this.id = id;
    }

    public Transaction(Long id, String accountNumber, BigDecimal amount, TransactionType type, LocalDateTime timestamp, String description) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
    }
}