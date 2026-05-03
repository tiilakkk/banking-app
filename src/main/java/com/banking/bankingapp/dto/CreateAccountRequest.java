package com.banking.bankingapp.dto;

import com.banking.bankingapp.entity.Account;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @NotNull(message = "Initial deposit is required")
    @Min(value = 0, message = "Initial deposit must be positive")
    private Double initialDeposit;

    @NotBlank(message = "PIN is required")
    @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
    @Pattern(regexp = "\\d{4}", message = "PIN must be 4 digits")
    private String pin;
}
