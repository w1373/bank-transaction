package com.bank.transaction.model;

import com.bank.transaction.exception.BusinessException;
import java.time.LocalDateTime;

/**
 * 统一API响应格式
 * @param <T> 响应数据类型
 */
public record Response<T>(
    int code,
    String message,
    T data
) {
    /**
     * 成功响应
     * @param data
     * @return
     * @param <T>
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "SUCCESS",  data);
    }

    /**
     * 异常
     * @param code
     * @param message
     * @param data
     * @return
     * @param <T>
     */
    public static <T> Response<T> error(int code,String message,T data) {
        return new Response<>(code,message,data);
    }

    /**
     * 异常转换
     * @param ex
     * @return
     */
    public static Response<?> fromException(BusinessException ex) {
        return new Response<>(
            ex.getCode(),
            ex.getMessage(),
            ex.getData()
        );
    }
}