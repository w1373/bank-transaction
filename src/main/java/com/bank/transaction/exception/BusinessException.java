package com.bank.transaction.exception;

import com.bank.transaction.enums.ExceptionType;
import lombok.Getter;

/**
 * 业务异常类
 * <p>用于处理业务逻辑中的异常情况</p>
 */
@Getter
public class BusinessException extends RuntimeException {
    /** 异常状态码 */
    private final int code;
    /** 附加数据 */
    private final Object data;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }

    public BusinessException(ExceptionType type,Object ... params) {
        super(String.format(type.getMessage(),params));
        this.code = type.getCode();
        this.data = null;
    }

    public BusinessException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }
}