package com.example.uts_loan.services.get;

import java.util.List;

import com.example.uts_loan.dto.GetLoanActiveDto;
import com.example.uts_loan.dto.GetLoanReportDto;
import com.example.uts_loan.dto.GetLoanRepotByCustomerTypeDto;
import com.example.uts_loan.dto.GetPersonalLoanHistoryDto;
import com.example.uts_loan.dto.GetPersonalPaymentHistoryDto;

public interface GetService {
    List<GetLoanActiveDto> getActiveLoans();
    GetLoanReportDto loanReport();
    GetLoanRepotByCustomerTypeDto loanRepotByCustomerType(String customerType);
    public List<GetPersonalLoanHistoryDto<GetPersonalPaymentHistoryDto>> personalLoanHistorysByAccountNumber(String accountNumber);
}
