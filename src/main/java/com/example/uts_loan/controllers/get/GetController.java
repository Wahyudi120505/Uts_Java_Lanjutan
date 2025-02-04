package com.example.uts_loan.controllers.get;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.uts_loan.dto.GenericResponse;
import com.example.uts_loan.dto.GetLoanActiveDto;
import com.example.uts_loan.dto.GetPersonalLoanHistoryDto;
import com.example.uts_loan.dto.GetPersonalPaymentHistoryDto;
import com.example.uts_loan.services.get.GetService;

@RestController
public class GetController {
    @Autowired
    GetService getService;

    @GetMapping("loan-active")
    public ResponseEntity<Object> findLoansActive(){
        try {
            List<GetLoanActiveDto> allLoansActive = getService.getActiveLoans();
            if (allLoansActive.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.failed("No data found", null));
            }
            return ResponseEntity.ok().body(GenericResponse.success("Successfully Loaded Data", allLoansActive));
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.failed(e.getMessage(), null));
        }
    }

    @GetMapping("load-report")
    public ResponseEntity<Object> loanReport(){
        try {
            return ResponseEntity.ok().body(GenericResponse.success("Successfully Loaded Data", getService.loanReport()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.failed(e.getMessage(), null));
        }
    }

    @GetMapping("loan-report-by-costumer-type/{customerType}")
    public ResponseEntity<Object> loanReportByCutomerType(@PathVariable String customerType){
        try {
            return ResponseEntity.ok().body(GenericResponse.success("Successfully Loaded Data", getService.loanRepotByCustomerType(customerType)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.failed(e.getMessage(), null));
        }
    }

    @GetMapping("personal-loan-history/{accountNumber}")
    public ResponseEntity<Object> loanHistoryByCutomerType(@PathVariable String accountNumber){
        try {
            List<GetPersonalLoanHistoryDto<GetPersonalPaymentHistoryDto>> history = getService.personalLoanHistorysByAccountNumber(accountNumber);
            if (history.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.failed("No data found", null));
            }
            return ResponseEntity.ok().body(GenericResponse.success("Successfully Loaded Data", history));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponse.failed(e.getMessage(), null));
        }
    }

}
