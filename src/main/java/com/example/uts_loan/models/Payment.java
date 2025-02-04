package com.example.uts_loan.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer paymentId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private LocalDate paymentDate; 

    @Column(nullable = false)
    private String paymentMethod; 

    @ManyToOne
    @JoinColumn(name = "loan_id", referencedColumnName = "loanId", nullable = false)
    private Loan loans;
}
