package com.example.uts_loan.services.get;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.uts_loan.dto.GetLoanActiveDto;
import com.example.uts_loan.dto.GetLoanReportDto;
import com.example.uts_loan.dto.GetLoanRepotByCustomerTypeDto;
import com.example.uts_loan.dto.GetPersonalLoanHistoryDto;
import com.example.uts_loan.dto.GetPersonalPaymentHistoryDto;
import com.example.uts_loan.models.Loan;
import com.example.uts_loan.models.Payment;
import com.example.uts_loan.repositorys.LoanRepository;
import com.example.uts_loan.repositorys.PaymentRepository;

@Service
public class GetServiceImpl implements GetService{
    @Autowired
    LoanRepository loanRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public List<GetLoanActiveDto> getActiveLoans() {
        List<Loan> loans = loanRepository.findAllByStatus("Active");
        return loans.stream()
                .map(this::mapToLoanActiveDto)
                .toList();
    }

    @Override
    public GetLoanReportDto loanReport() {
        Integer totalUnPaid = loanRepository.sumRemainingAmount();    
        Integer totalPaid = paymentRepository.sumPaidAmount();

        return GetLoanReportDto.builder()
            .totalPaid(totalPaid)
            .totalUnPaid(totalUnPaid)
            .build();
    }

    @Override
    public GetLoanRepotByCustomerTypeDto loanRepotByCustomerType(String customerType) {
        Integer totalUnPaid = loanRepository.sumRemainingAmountByCustomerType(customerType);    
        Integer totalPaid = paymentRepository.sumPaidAmountByCustomerType(customerType);

        return GetLoanRepotByCustomerTypeDto.builder()
            .customerType(customerType)
            .totalPaid(totalPaid)
            .totalUnpaid(totalUnPaid)
            .build();
    }

    @Override
    public List<GetPersonalLoanHistoryDto<GetPersonalPaymentHistoryDto>> personalLoanHistorysByAccountNumber(String accountNumber) {
        List<Loan> loans = loanRepository.findLoansByAccountNumber(accountNumber);
        List<Payment> payments = paymentRepository.findPaymentByAccountNumber(accountNumber);
    
        List<GetPersonalLoanHistoryDto<GetPersonalPaymentHistoryDto>> loanHistory = new ArrayList<>();
    
        for (Loan loan : loans) {
            List<Payment> loanPayments = payments.stream()
                .filter(payment -> payment.getLoans().getLoanId().equals(loan.getLoanId()))
                .collect(Collectors.toList());
    
            List<GetPersonalPaymentHistoryDto> paymentHistoryDtos = loanPayments.stream()
                .map(payment -> GetPersonalPaymentHistoryDto.builder()
                    .paymentId(payment.getPaymentId())
                    .amount(payment.getAmount())
                    .paymentDate(payment.getPaymentDate().toString())  
                    .paymentMethod(payment.getPaymentMethod())
                    .build())
                .collect(Collectors.toList());
    
            GetPersonalLoanHistoryDto<GetPersonalPaymentHistoryDto> dto = GetPersonalLoanHistoryDto.<GetPersonalPaymentHistoryDto>builder()
                .loanId(loan.getLoanId())
                .amount(loan.getAmount())
                .remain(loan.getRemain())
                .startDate(loan.getStartDate().toString())  
                .dueDate(loan.getDueDate().toString())  
                .tenor(loan.getTenor())
                .status(loan.getStatus())
                .paymentHistory(paymentHistoryDtos)  
                .build();
    
            loanHistory.add(dto);
        }
    
        return loanHistory;
    }
                
    GetLoanActiveDto mapToLoanActiveDto(Loan loan){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateFormatted = loan.getStartDate().format(formatter);
        String dueDateFormatted = loan.getDueDate().format(formatter);

        return GetLoanActiveDto.builder()
            .loanId(loan.getLoanId())
            .customerId(loan.getCustomers().getCustomerId())
            .accountNumber(loan.getCustomers().getAccountNumber())
            .customerName(loan.getCustomers().getCustomerName())
            .amount(loan.getAmount())
            .remain(loan.getRemain())
            .startDate(startDateFormatted)
            .dueDate(dueDateFormatted)
            .status("Active")
            .build();
    }
}
