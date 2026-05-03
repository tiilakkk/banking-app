package com.banking.bankingapp.controller;

import com.banking.bankingapp.dto.*;
import com.banking.bankingapp.entity.*;
import com.banking.bankingapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(userDetails.getUsername(), request);
        return ResponseEntity.ok(AccountResponse.from(account));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<AccountResponse> accounts = accountService
                .getUserAccounts(userDetails.getUsername())
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {
        Transaction t = accountService.deposit(userDetails.getUsername(), request);
        return ResponseEntity.ok(TransactionResponse.from(t));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {
        Transaction t = accountService.withdraw(userDetails.getUsername(), request);
        return ResponseEntity.ok(TransactionResponse.from(t));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {
        accountService.transfer(userDetails.getUsername(), request);
        return ResponseEntity.ok().body("Transfer successful!");
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String accountNumber) {
        List<TransactionResponse> transactions = accountService
                .getTransactions(userDetails.getUsername(), accountNumber)
                .stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
    }
}