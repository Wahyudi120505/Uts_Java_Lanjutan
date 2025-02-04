package com.example.uts_loan.services.payment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.payment.PaymentRequest;
import com.example.uts_loan.dto.payment.PaymentResponse;
import com.example.uts_loan.models.Loan;
import com.example.uts_loan.models.Payment;
import com.example.uts_loan.repositorys.LoanRepository;
import com.example.uts_loan.repositorys.PaymentRepository;
import com.example.uts_loan.repositorys.specification.PaymentSpecification;

@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private LoanRepository loanRepository;

    @Transactional
    @Override
    public void postPayment(PaymentRequest request) {
        if (request.getLoanId() == null || request.getAmount() == null || request.getPaymentMethod() == null) {
            throw new IllegalArgumentException("All fields must be filled!");
        }
    
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + request.getLoanId() + " not found!"));
    
        if (loan.getRemain() < request.getAmount()) {
            throw new IllegalArgumentException("The payment amount exceeds the remaining loan balance!");
        }
    
        Payment payment = Payment.builder()
                .loans(loan)
                .amount(request.getAmount())
                .paymentDate(LocalDate.now())
                .paymentMethod(request.getPaymentMethod())
                .build();
    
        loan.setRemain(loan.getRemain() - request.getAmount());
        
        if (loan.getRemain() == 0) {
            loan.setStatus("Completed");
        } else if (LocalDate.now().isAfter(loan.getDueDate())) {
            loan.setStatus("Overdue");
        } else {
            loan.setStatus("Active");
        }
    
        paymentRepository.save(payment);
        loanRepository.save(loan);
    }
    
    @Transactional
    @Override
    public void putPayment(Integer id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment with ID " + id + " not found!"));
        
        if (request.getLoanId() == null || request.getAmount() == null || request.getPaymentMethod() == null) {
            throw new IllegalArgumentException("All fields must be filled!");
        }
    
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + request.getLoanId() + " not found!"));
        
        if (payment.getLoans().equals(loan)) {
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentDate(LocalDate.now());
            paymentRepository.save(payment);
    
            Integer totalAmount = paymentRepository.findAllPaymentsByLoans(loan)
                    .stream().mapToInt(Payment::getAmount).sum();
            
            loan.setRemain(loan.getAmount() - totalAmount);
            
            if (loan.getRemain() == 0) {
                loan.setStatus("Completed");
            } else if (LocalDate.now().isAfter(loan.getDueDate())) {
                loan.setStatus("Overdue");
            } else {
                loan.setStatus("Active");
            }
            
            loanRepository.save(loan);
        } else {
            Loan previousLoan = loanRepository.findLoanByLoanId(payment.getLoans().getLoanId());
            previousLoan.setRemain(previousLoan.getRemain() + payment.getAmount());
            previousLoan.setStatus("Active");
            loanRepository.save(previousLoan);
    
            if (loan.getRemain() >= payment.getAmount()) {
                loan.setRemain(loan.getRemain() - payment.getAmount());
            } else {
                throw new IllegalArgumentException("Payment amount exceeds remaining loan balance!");
            }
            
            if (loan.getRemain() == 0) {
                loan.setStatus("Completed");
            } else if (LocalDate.now().isAfter(loan.getDueDate())) {
                loan.setStatus("Overdue");
            } else {
                loan.setStatus("Active");
            }
            
            loanRepository.save(loan);
            payment.setLoans(loan);
            paymentRepository.save(payment);
        }
    }

    @Transactional
    @Override
    public void deletePayment(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment log with ID " + id + " not found"));

        Loan loan = payment.getLoans();
        if (loan != null) {
            loan.setRemain(loan.getRemain() + payment.getAmount());
            loanRepository.save(loan);
        }
        
        paymentRepository.delete(payment);
    }

    @Override
    public PageResponse<PaymentResponse> findAllWithFilter(Pageable pageable, Integer loanId, String paymentDate,
            String paymentMethod) {
        Specification<Payment> spec = Specification.where(null);
        
        if (loanId != null) {
            spec = spec.and(PaymentSpecification.loanId(loanId));
        }
        if (paymentDate != null && !paymentDate.isEmpty()) {
            spec = spec.and(PaymentSpecification.paymentDate(paymentDate));
        }
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            spec = spec.and(PaymentSpecification.paymentMethod(paymentMethod));
        }

        Page<Payment> paymentPage = paymentRepository.findAll(spec, pageable);
        return PageResponse.<PaymentResponse>builder()
            .page(pageable.getPageNumber())
            .size(pageable.getPageSize())
            .totalPage(paymentPage.getTotalPages())
            .totalItem(paymentPage.getTotalElements())
            .items(paymentPage.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList()))
            .build();
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .loanId(payment.getLoans().getLoanId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }
}
