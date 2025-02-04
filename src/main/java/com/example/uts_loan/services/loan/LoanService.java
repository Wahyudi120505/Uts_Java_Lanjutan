package com.example.uts_loan.services.loan;

import org.springframework.data.domain.Pageable;

import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.loan.LoanRequest;
import com.example.uts_loan.dto.loan.LoanResponse;

public interface LoanService {
    void postLoan(LoanRequest request);
    PageResponse<LoanResponse> findAllWithFilter(Pageable pageable, Integer customerId, String tenor, String status);
    void putLoan(Integer id, LoanRequest requestUpdate);
    void deleteLoan(Integer id);
}
