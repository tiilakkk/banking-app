package com.banking.bankingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TransactionRequest {
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Double amount;

    @NotBlank(message = "PIN is required")
    private String pin;

    // For transfers only
    private String toAccountNumber;
}