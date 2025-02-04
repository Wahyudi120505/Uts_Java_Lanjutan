package com.example.uts_loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetLoanRepotByCustomerTypeDto {
    String customerType;
    Integer totalPaid;
    Integer totalUnpaid;
}
