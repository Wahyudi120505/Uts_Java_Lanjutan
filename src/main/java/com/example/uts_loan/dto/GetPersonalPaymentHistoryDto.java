package com.example.uts_loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPersonalPaymentHistoryDto {
    private Integer paymentId;
    private Integer amount;
    private String paymentDate;
    private String paymentMethod;
}
