package com.bank.transaction.exception;

import com.bank.transaction.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<?>> handleBusinessException(BusinessException exception) {
        return new ResponseEntity<>(Response.fromException(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(Response.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}    