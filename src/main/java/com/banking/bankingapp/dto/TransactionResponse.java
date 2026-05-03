package com.banking.bankingapp.dto;

import com.banking.bankingapp.entity.Transaction;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private String type;
    private Double amount;
    private Double balanceAfter;
    private String description;
    private LocalDateTime timestamp;

    public static TransactionResponse from(Transaction t) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setType(t.getType().name());
        response.setAmount(t.getAmount());
        response.setBalanceAfter(t.getBalanceAfter());
        response.setDescription(t.getDescription());
        response.setTimestamp(t.getTimestamp());
        return response;
    }
}