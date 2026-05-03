package com.banking.bankingapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loanNumber;

    @Column(nullable = false)
    private Double principal;

    @Column(nullable = false)
    private Double annualInterestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false)
    private Double emiAmount;

    @Column(nullable = false)
    private Double outstandingBalance;

    private Integer emisPaid = 0;

    @Column(nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public enum LoanStatus { ACTIVE, CLOSED }
}