package com.example.uts_loan.repositorys.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.uts_loan.models.Payment;

public class PaymentSpecification {
    public static Specification<Payment> loanId(Integer loanId){
        return (root, query, builder) -> builder.equal(root.get("loans").get("loanId"), loanId);
    }

    public static Specification<Payment> paymentDate(String paymentDate){
        return (root, query, builder) -> builder.equal(root.get("paymentDate"), paymentDate);
    }

    public static Specification<Payment> paymentMethod(String paymentMethod){
        return (root, query, builder) -> builder.equal(root.get("paymentMethod"), paymentMethod);
    }
}
