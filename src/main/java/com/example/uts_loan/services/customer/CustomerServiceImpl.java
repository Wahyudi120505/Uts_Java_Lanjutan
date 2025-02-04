package com.example.uts_loan.services.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.customer.CustomerRequest;
import com.example.uts_loan.dto.customer.CustomerResponse;
import com.example.uts_loan.models.Customer;
import com.example.uts_loan.models.Loan;
import com.example.uts_loan.models.Payment;
import com.example.uts_loan.repositorys.CustomerRepository;
import com.example.uts_loan.repositorys.LoanRepository;
import com.example.uts_loan.repositorys.PaymentRepository;
import com.example.uts_loan.repositorys.specification.CustomerSpecification;

import jakarta.transaction.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void postCustomer(CustomerRequest request) {
        if (request.getAccountNumber() == null || request.getCustomerName() == null ||
                request.getPhone() == null || request.getAddress() == null || request.getCustomerType() == null) {
            throw new IllegalArgumentException("All fields must be filled!");
        }

        if (customerRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number has been registered!");
        }

        if (!request.getAccountNumber().matches("\\d{8}")) {
            throw new IllegalArgumentException("Account number must be 8 digits!");
        }

        if (!request.getPhone().matches("\\d{12}")) {
            throw new IllegalArgumentException("Phone number must be 12 digits!");
        }

        Customer addCustomer = Customer.builder()
                .accountNumber(request.getAccountNumber())
                .customerName(request.getCustomerName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .customerType(request.getCustomerType())
                .build();

        customerRepository.save(addCustomer);
    }

    @Override
    public PageResponse<CustomerResponse> findAllWithFilter(Pageable pageable, String accountNumber, String customerName,
            String customerType) {
        Specification<Customer> spac = Specification.where(null);
        if (customerName != null && !customerName.isEmpty()) {
            spac = spac.and(CustomerSpecification.customerName(customerName));
        }
        if (accountNumber != null && !accountNumber.isEmpty()) {
            spac = spac.and(CustomerSpecification.accountNumber(accountNumber));
        }
        if (customerType != null && !customerType.isEmpty()) {
            spac = spac.and(CustomerSpecification.customerType(customerType));
        }
        Page<Customer> customerPage = customerRepository.findAll(spac, pageable);
        return PageResponse.<CustomerResponse>builder()
            .page(pageable.getPageNumber())
            .size(pageable.getPageSize())
            .totalPage(customerPage.getTotalPages())
            .totalItem(customerPage.getTotalElements())
            .items(customerPage.stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList()))
            .build();
    }

    @Override
    public void putCustomer(Integer id, CustomerRequest requestUpdate) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            Customer ctr = customer.get();

            if (requestUpdate.getAccountNumber() == null || requestUpdate.getCustomerName() == null ||
                    requestUpdate.getPhone() == null || requestUpdate.getAddress() == null
                    || requestUpdate.getCustomerType() == null) {
                throw new IllegalArgumentException("All fields must be filled!");
            }

            if (customerRepository.existsByAccountNumber(requestUpdate.getAccountNumber())) {
                throw new IllegalArgumentException("Account number has been registered!");
            }

            if (!requestUpdate.getAccountNumber().matches("\\d{8}")) {
                throw new IllegalArgumentException("Account number must be 8 digits!");
            }

            if (!requestUpdate.getPhone().matches("\\d{12}")) {
                throw new IllegalArgumentException("Phone number must be 12 digits!");
            }

            ctr.setAccountNumber(requestUpdate.getAccountNumber());
            ctr.setCustomerName(requestUpdate.getCustomerName());
            ctr.setPhone(requestUpdate.getPhone());
            ctr.setAddress(requestUpdate.getAddress());
            ctr.setCustomerType(requestUpdate.getCustomerType());

            customerRepository.save(ctr);
        } else {
            throw new IllegalArgumentException("Customer log with ID " + id + " not found");
        }
    }

    @Transactional
    @Override
    public void deleteCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + customerId + " not found"));
    
        List<Loan> loans = loanRepository.findAllLoansByCustomers(customer);
    
        List<Payment> payments = new ArrayList<>();
        for (Loan loan : loans) {
            payments.addAll(paymentRepository.findAllPaymentsByLoans(loan));
        }
    
        for (Payment payment : payments) {
            if (payment.getLoans() == null) {
                throw new IllegalStateException("Payment with no associated Loan");
            }
        }
        
        if (!payments.isEmpty()) {
            paymentRepository.deleteAll(payments);
        }

        if (!loans.isEmpty()) {
            loanRepository.deleteAll(loans);
        }
    
        customerRepository.delete(customer);
    }
    
    public CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .accountNumber(customer.getAccountNumber())
                .customerName(customer.getCustomerName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .customerType(customer.getCustomerType())
                .build();
    }
}
