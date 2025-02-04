package com.example.uts_loan.services.customer;

import org.springframework.data.domain.Pageable;

import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.customer.CustomerRequest;
import com.example.uts_loan.dto.customer.CustomerResponse;

public interface CustomerService {
    void postCustomer(CustomerRequest request);
    PageResponse<CustomerResponse> findAllWithFilter(Pageable pageable, String accountNumber, String customerName, String customerTtype);
    void putCustomer(Integer id, CustomerRequest requestUpdate);
    void deleteCustomer(Integer id);
}
