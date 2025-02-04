package com.example.uts_loan.repositorys.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.uts_loan.models.Customer;

public class CustomerSpecification {
    public static Specification<Customer> accountNumber(String accountNumber){
        return (root, query, builder) -> builder.like(root.get("accountNumber"), accountNumber);
    }
    public static Specification<Customer> customerName(String customerName){
        return (root, query, builder) -> builder.like(root.get("customerName"), "%" + customerName + "%");
    }
    public static Specification<Customer> customerType(String customerType){
        return (root, query, builder) -> builder.equal(root.get("customerType"), customerType);
    }
}
