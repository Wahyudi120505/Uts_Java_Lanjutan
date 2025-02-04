package com.example.uts_loan.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private Integer customerId;
    private String accountNumber;
    private String customerName;
    private String phone;
    private String address;
    private String customerType;
}
