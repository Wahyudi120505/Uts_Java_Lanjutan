package com.example.uts_loan.repositorys;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.uts_loan.models.Customer;
import com.example.uts_loan.models.Loan;


public interface LoanRepository extends JpaRepository<Loan,Integer>{
    Page<Loan> findAll(Specification<Loan> spac, Pageable pageable);
    Loan findLoanByLoanId(Integer id);
    List<Loan> findAllLoansByCustomers(Customer customer);

    List<Loan> findAllByStatus(String status);

    @Query("SELECT SUM(l.remain) FROM Loan l")
    Integer sumRemainingAmount();

    @Query("SELECT SUM(l.remain) FROM Loan l JOIN l.customers c WHERE c.customerType = :customerType")
    Integer sumRemainingAmountByCustomerType(String customerType);
        
    @Query("SELECT l FROM Loan l JOIN l.customers c WHERE c.accountNumber = :accountNumber")
    List<Loan> findLoansByAccountNumber(String accountNumber);
        
}
