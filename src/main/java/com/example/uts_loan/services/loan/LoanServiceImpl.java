package com.example.uts_loan.services.loan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.loan.LoanRequest;
import com.example.uts_loan.dto.loan.LoanResponse;
import com.example.uts_loan.models.Customer;
import com.example.uts_loan.models.Loan;
import com.example.uts_loan.models.Payment;
import com.example.uts_loan.repositorys.CustomerRepository;
import com.example.uts_loan.repositorys.LoanRepository;
import com.example.uts_loan.repositorys.PaymentRepository;
import com.example.uts_loan.repositorys.specification.LoanSpecification;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public void postLoan(LoanRequest request) {
        if (request.getCustomerId() == null || request.getAmount() == null ||
                request.getTenor() == null) {
            throw new IllegalArgumentException("All fields must be filled!");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Jumlah pinjaman harus lebih besar dari 0!");
        }

        Optional<Customer> customerOpt = customerRepository.findById(request.getCustomerId());
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer with ID " + request.getCustomerId() + " not found!");
        }

        Customer customer = customerOpt.get();

        LocalDate startDate = LocalDate.now();
        LocalDate dueDate = calculateDueDate(startDate, request.getTenor());

        Loan loan = Loan.builder()
                .amount(request.getAmount())
                .remain(request.getAmount())
                .startDate(startDate)
                .dueDate(dueDate)
                .tenor(request.getTenor())
                .status("Active")
                .customers(customer)
                .build();

        loanRepository.save(loan);
    }

    @Override
    public PageResponse<LoanResponse> findAllWithFilter(Pageable pageable, Integer customerId, String tenor, String status) {
        Specification<Loan> spac = Specification.where(null);
        if (customerId != null) {
            spac = spac.and(LoanSpecification.customerId(customerId));
        }

        if (tenor != null && !tenor.isEmpty()) {
            spac = spac.and(LoanSpecification.tenor(tenor));
        }

        if (status != null && !status.isEmpty()) {
            spac = spac.and(LoanSpecification.status(status));
        }

        Page<Loan> loanPage = loanRepository.findAll(spac, pageable);

        loanPage.getContent().forEach(loan -> {
            if (LocalDate.now().isEqual(loan.getDueDate()) || LocalDate.now().isAfter(loan.getDueDate())) {
                loan.setStatus("Overdue");
                loanRepository.save(loan);
            }

            if (loan.getRemain() == 0) {
                loan.setStatus("Complated");
                loanRepository.save(loan);
            }
        });

        return PageResponse.<LoanResponse>builder()
            .page(pageable.getPageNumber())
            .size(pageable.getPageSize())
            .totalPage(loanPage.getTotalPages())
            .totalItem(loanPage.getTotalElements())
            .items(loanPage.stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList()))
            .build();
    }

    @Override
    public void putLoan(Integer id, LoanRequest requestUpdate) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + id + " not found!"));

        if (requestUpdate.getCustomerId() == null || requestUpdate.getAmount() == null
                || requestUpdate.getTenor() == null) {
            throw new IllegalArgumentException("All fields must be filled!");
        }

        if (requestUpdate.getAmount() <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than 0!");
        }

        Customer customer = customerRepository.findById(requestUpdate.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer with ID " + requestUpdate.getCustomerId() + " not found!"));

        LocalDate dueDate = calculateDueDate(loan.getStartDate(), requestUpdate.getTenor());

        loan.setCustomers(customer);
        loan.setAmount(requestUpdate.getAmount());
        loan.setRemain(requestUpdate.getAmount());
        loan.setDueDate(dueDate);
        loan.setTenor(requestUpdate.getTenor());

        if (loan.getRemain() == 0) {
            loan.setStatus("Complated");
            loanRepository.save(loan);
        }

        if (LocalDate.now().isEqual(loan.getDueDate()) || LocalDate.now().isAfter(loan.getDueDate())) {
            loan.setStatus("Overdue");
        } else {
            loan.setStatus("Active");
        }

        loanRepository.save(loan);
    }

    @Override
    @Transactional
    public void deleteLoan(Integer id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan with ID " + id + " not found!"));

        List<Payment> payments = paymentRepository.findAllPaymentsByLoans(loan);
        if (!payments.isEmpty()) {
            paymentRepository.deleteAll(payments);
        }

        loanRepository.delete(loan);
    }

    private LoanResponse mapToLoanResponse(Loan loan) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateFormatted = loan.getStartDate().format(formatter);
        String dueDateFormatted = loan.getDueDate().format(formatter);

        return LoanResponse.builder()
                .loanid(loan.getLoanId())
                .amount(loan.getAmount())
                .remain(loan.getRemain())
                .startDate(startDateFormatted)
                .dueDate(dueDateFormatted)
                .tenor(loan.getTenor())
                .status(loan.getStatus())
                .customerId(loan.getCustomers().getCustomerId())
                .build();
    }

    private LocalDate calculateDueDate(LocalDate startDate, String tenor) {
        String[] parts = tenor.split(" ");
        int duration = Integer.parseInt(parts[0]);
        String unit = parts[1].toLowerCase();

        switch (unit) {
            case "week":
            case "Week":
                return startDate.plusWeeks(duration);
            case "day":
            case "Day":
                return startDate.plusDays(duration);
            case "month":
            case "Month":
                return startDate.plusMonths(duration);
            default:
                throw new IllegalArgumentException("Invalid tenor format. Use 'days', 'weeks', or 'months'.");
        }
    }
}
