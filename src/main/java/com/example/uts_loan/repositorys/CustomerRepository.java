package com.example.uts_loan.repositorys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.uts_loan.models.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{
    boolean existsByAccountNumber(String accountNumber);
    Page<Customer> findAll(Specification<Customer> spac, Pageable pageable);

}
