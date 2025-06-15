package com.bank.transaction.enums;

import lombok.Getter;

@Getter
public enum ExceptionType {
    TRANSACTION_DUPLICATE(1001,"创建交易重复：[%s]"),
    TRANSACTION_NOT_EXIST(1002,"交易不存在：[%s]"),
    ;

    private final int code;
    private final String message;

    ExceptionType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
