package com.bank.transaction.dto;

import com.bank.transaction.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    @NotBlank(message = "账号不能为空")
    private String accountNumber;

    @Positive(message = "金额必须大于零")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @PastOrPresent
    private LocalDateTime timestamp;

    @Size(max = 255)
    private String description;
}
