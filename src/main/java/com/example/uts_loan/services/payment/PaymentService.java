package com.example.uts_loan.services.payment;

import org.springframework.data.domain.Pageable;

import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.payment.PaymentRequest;
import com.example.uts_loan.dto.payment.PaymentResponse;

public interface PaymentService {
    void postPayment(PaymentRequest request);
    void putPayment(Integer id, PaymentRequest request);
    void deletePayment(Integer id);
    PageResponse<PaymentResponse> findAllWithFilter(Pageable pageable, Integer loanId, String paymentDate, String paymentMethod);
}
