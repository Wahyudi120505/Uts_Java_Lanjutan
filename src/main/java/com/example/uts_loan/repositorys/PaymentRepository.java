package com.example.uts_loan.repositorys;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.uts_loan.models.Loan;
import com.example.uts_loan.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Integer>{
    List<Payment> findAllPaymentsByLoans(Loan loan);
    Page<Payment> findAll(Specification<Payment> spac, Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p")
    Integer sumPaidAmount();

    @Query("SELECT SUM(p.amount) FROM Payment p JOIN p.loans l JOIN l.customers c WHERE c.customerType = :customerType")
    Integer sumPaidAmountByCustomerType(String customerType);

    @Query("SELECT p FROM Payment p JOIN p.loans l JOIN l.customers c WHERE c.accountNumber = :accountNumber")
    List<Payment> findPaymentByAccountNumber(String accountNumber);


}
