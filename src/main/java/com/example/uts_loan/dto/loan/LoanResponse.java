package com.example.uts_loan.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanResponse {
    private Integer loanid;
    private Integer customerId;
    private Integer amount; 
    private Integer remain; 
    private String startDate;
    private String dueDate;
    private String tenor;
    private String status;
}
