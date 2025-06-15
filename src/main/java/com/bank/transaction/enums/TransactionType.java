package com.bank.transaction.enums;

import lombok.Getter;

/**
 * 交易类型枚举
 */
@Getter
public enum TransactionType {
    DEPOSIT("存款"),      // 资金存入账户
    WITHDRAWAL("取款"),   // 从账户提取资金
    TRANSFER("转账"),     // 账户间资金转移
    PAYMENT("支付");      // 商业交易付款

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }
}