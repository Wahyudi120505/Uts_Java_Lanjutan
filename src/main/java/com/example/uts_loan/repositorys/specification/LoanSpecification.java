package com.example.uts_loan.repositorys.specification;

import org.springframework.data.jpa.domain.Specification;
import com.example.uts_loan.models.Loan;

public class LoanSpecification {
    public static Specification<Loan> customerId(Integer customerId){
        return (root, query, builder) -> builder.equal(root.get("customers").get("customerId"), customerId);
    }

    public static Specification<Loan> tenor(String tenor){
        return (root, query, builder) -> builder.equal(root.get("tenor"), tenor);
    }

    public static Specification<Loan> status(String status){
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }
}
