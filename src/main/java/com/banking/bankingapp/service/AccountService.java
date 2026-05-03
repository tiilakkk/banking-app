package com.banking.bankingapp.service;

import com.banking.bankingapp.dto.*;
import com.banking.bankingapp.entity.*;
import com.banking.bankingapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Create a new account
    public Account createAccount(String email, CreateAccountRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate minimum deposit
        if (request.getAccountType() == Account.AccountType.SAVINGS
                && request.getInitialDeposit() < 500)
            throw new RuntimeException("Minimum deposit for Savings is ₹500");
        if (request.getAccountType() == Account.AccountType.CURRENT
                && request.getInitialDeposit() < 5000)
            throw new RuntimeException("Minimum deposit for Current is ₹5000");

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber(request.getAccountType()));
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit());
        account.setPin(passwordEncoder.encode(request.getPin()));
        account.setUser(user);
        return accountRepository.save(account);
    }

    // Get all accounts for a user
    @Transactional(readOnly = true)
    public List<Account> getUserAccounts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        // Force load user for each account
        accounts.forEach(acc -> acc.getUser().getFullName());
        return accounts;
    }

    // Deposit
    @Transactional
    public Transaction deposit(String email, TransactionRequest request) {
        Account account = getAndVerifyAccount(request.getAccountNumber(), email, request.getPin());
        account.setBalance(account.getBalance() + request.getAmount());
        accountRepository.save(account);
        return saveTransaction(account, Transaction.TransactionType.DEPOSIT,
                request.getAmount(), "Deposit");
    }

    // Withdraw
    @Transactional
    public Transaction withdraw(String email, TransactionRequest request) {
        Account account = getAndVerifyAccount(request.getAccountNumber(), email, request.getPin());

        double minBalance = account.getAccountType() == Account.AccountType.SAVINGS ? 500 : 0;
        if (account.getBalance() - request.getAmount() < minBalance)
            throw new RuntimeException("Insufficient funds. Minimum balance ₹" + minBalance + " required.");

        account.setBalance(account.getBalance() - request.getAmount());
        accountRepository.save(account);
        return saveTransaction(account, Transaction.TransactionType.WITHDRAWAL,
                request.getAmount(), "Withdrawal");
    }

    // Transfer
    @Transactional
    public void transfer(String email, TransactionRequest request) {
        Account from = getAndVerifyAccount(request.getAccountNumber(), email, request.getPin());
        Account to = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        double minBalance = from.getAccountType() == Account.AccountType.SAVINGS ? 500 : 0;
        if (from.getBalance() - request.getAmount() < minBalance)
            throw new RuntimeException("Insufficient funds.");

        from.setBalance(from.getBalance() - request.getAmount());
        to.setBalance(to.getBalance() + request.getAmount());
        accountRepository.save(from);
        accountRepository.save(to);

        saveTransaction(from, Transaction.TransactionType.TRANSFER_OUT,
                request.getAmount(), "Transfer to " + to.getAccountNumber());
        saveTransaction(to, Transaction.TransactionType.TRANSFER_IN,
                request.getAmount(), "Transfer from " + from.getAccountNumber());
    }

    // Get transaction history
    public List<Transaction> getTransactions(String email, String accountNumber) {
        Account account = getAndVerifyOwnership(accountNumber, email);
        return transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());
    }

    // Helper — get account and verify PIN and ownership
    private Account getAndVerifyAccount(String accountNumber, String email, String pin) {
        Account account = getAndVerifyOwnership(accountNumber, email);

        if (account.getStatus() == Account.AccountStatus.LOCKED)
            throw new RuntimeException("Account is locked. Please contact support.");

        if (!passwordEncoder.matches(pin, account.getPin())) {
            account.setFailedPinAttempts(account.getFailedPinAttempts() + 1);
            if (account.getFailedPinAttempts() >= 3) {
                account.setStatus(Account.AccountStatus.LOCKED);
                accountRepository.save(account);
                throw new RuntimeException("Account locked due to too many wrong PIN attempts.");
            }
            accountRepository.save(account);
            int remaining = 3 - account.getFailedPinAttempts();
            throw new RuntimeException("Wrong PIN. " + remaining + " attempt(s) remaining.");
        }

        account.setFailedPinAttempts(0);
        return account;
    }

    // Helper — verify account belongs to user
    private Account getAndVerifyOwnership(String accountNumber, String email) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        if (!account.getUser().getEmail().equals(email))
            throw new RuntimeException("Unauthorized access to account.");
        return account;
    }

    // Helper — save transaction record
    private Transaction saveTransaction(Account account, Transaction.TransactionType type,
                                        double amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    // Helper — generate account number
    private String generateAccountNumber(Account.AccountType type) {
        String prefix = type == Account.AccountType.SAVINGS ? "SA" :
                type == Account.AccountType.CURRENT ? "CA" : "LA";
        String number;
        do {
            number = prefix + (100000 + (int)(Math.random() * 900000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}