package com.banking.bankingapp.dto;

import com.banking.bankingapp.entity.Account;
import lombok.Data;

@Data
public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private Double balance;
    private String status;
    private String holderName;

    public static AccountResponse from(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus().name());
        response.setHolderName(account.getUser() != null ?
                account.getUser().getFullName() : "");
        return response;
    }
}