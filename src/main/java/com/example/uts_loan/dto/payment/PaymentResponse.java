package com.example.uts_loan.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private Integer paymentId;
    private Integer loanId;
    private Integer amount;
    private String paymentDate; 
    private String paymentMethod; 
}
