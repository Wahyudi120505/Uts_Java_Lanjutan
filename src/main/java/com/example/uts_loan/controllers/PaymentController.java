package com.example.uts_loan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.uts_loan.dto.GenericResponse;
import com.example.uts_loan.dto.PageResponse;
import com.example.uts_loan.dto.payment.PaymentRequest;
import com.example.uts_loan.dto.payment.PaymentResponse;
import com.example.uts_loan.services.payment.PaymentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping()
    public ResponseEntity<Object> create(PaymentRequest request) {
        try {
            paymentService.postPayment(request);
            return ResponseEntity.ok().body(GenericResponse.success("Payment created succsessfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse.failed(e.getMessage(), null));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.failed("Internal server error", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody PaymentRequest request) {
        try {
            paymentService.putPayment(id, request);
            return ResponseEntity.ok().body(GenericResponse.success("Data successfully updated", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GenericResponse.failed(e.getMessage(), null));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.failed("Internal server error", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok().body(GenericResponse.success("Data successfully deleted", null));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.failed(e.getMessage(), null));
        }
    }

    @GetMapping()
    public ResponseEntity<Object> findAll(@RequestParam int page, @RequestParam int size,
            @RequestParam(required = false) Integer loanId, @RequestParam(required = false) String paymentDate,
            @RequestParam(required = false) String paymentMethod) {
        try {
            if (page < 0 || size <= 0) {
                return ResponseEntity.badRequest().body("Page and size must be positive numbers");
            }
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<PaymentResponse> paymentResponses = paymentService.findAllWithFilter(pageable, loanId,
                    paymentDate, paymentMethod);
            if (paymentResponses.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.failed("No data found", null));
            }
            return ResponseEntity.ok().body(GenericResponse.success("Payment fetched successfully", paymentResponses));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.failed(e.getMessage(), null));
        }
    }
}
