package com.banking.bankingapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "fixed_deposits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fdNumber;

    @Column(nullable = false)
    private Double principal;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Column(nullable = false)
    private Double maturityAmount;

    @Enumerated(EnumType.STRING)
    private FDStatus status = FDStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public enum FDStatus { ACTIVE, MATURED, CLOSED }
}