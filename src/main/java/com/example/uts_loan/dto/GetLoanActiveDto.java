package com.example.uts_loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetLoanActiveDto {
    private Integer loanId;
    private Integer customerId;
    private String accountNumber;
    private String customerName;
    private Integer amount;
    private Integer remain;
    private String startDate;
    private String dueDate;
    private String status;
}
