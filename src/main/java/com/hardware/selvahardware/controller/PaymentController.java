package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.payment.PaymentDto;
import com.hardware.selvahardware.dto.payment.PaymentRequest;
import com.hardware.selvahardware.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Transactions")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Get all payment transactions with optional filters")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getAll(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPayments(customerId, type, fromDate, toDate)));
    }

    @PostMapping
    @Operation(summary = "Record payment transaction")
    public ResponseEntity<ApiResponse<PaymentDto>> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(paymentService.createPayment(request), "Payment recorded"));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer payment history")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getCustomerPayments(customerId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment transaction")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Payment deleted"));
    }
}
