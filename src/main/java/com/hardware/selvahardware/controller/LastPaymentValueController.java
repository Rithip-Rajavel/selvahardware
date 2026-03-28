package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.lastpaymentvalue.LastPaymentValueDto;
import com.hardware.selvahardware.dto.lastpaymentvalue.LastPaymentValueRequest;
import com.hardware.selvahardware.service.LastPaymentValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/last-payment-values")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Last Payment Value Management", description = "APIs for managing customer last payment values")
public class LastPaymentValueController {

    private final LastPaymentValueService lastPaymentValueService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Create or update last payment value", description = "Create a new last payment value or update existing one for a customer and product")
    public ResponseEntity<ApiResponse<LastPaymentValueDto>> createOrUpdateLastPaymentValue(
            @Valid @RequestBody LastPaymentValueRequest request) {
        log.info("Creating or updating last payment value for customer: {} and product: {}", 
                request.getCustomerId(), request.getProductId());
        LastPaymentValueDto value = lastPaymentValueService.createOrUpdateLastPaymentValue(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(value, "Last payment value saved successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'EMPLOYEE', 'ACCOUNTANT', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get last payment values by customer", description = "Retrieve all last payment values for a specific customer")
    public ResponseEntity<ApiResponse<List<LastPaymentValueDto>>> getLastPaymentValuesByCustomerId(
            @PathVariable UUID customerId) {
        log.info("Fetching last payment values for customer: {}", customerId);
        List<LastPaymentValueDto> values = lastPaymentValueService.getLastPaymentValuesByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(values, "Last payment values retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}/product/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'EMPLOYEE', 'ACCOUNTANT', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get last payment value by customer and product", description = "Retrieve the last payment value for a specific customer and product")
    public ResponseEntity<ApiResponse<LastPaymentValueDto>> getLastPaymentValueByCustomerAndProduct(
            @PathVariable UUID customerId, @PathVariable UUID productId) {
        log.info("Fetching last payment value for customer: {} and product: {}", customerId, productId);
        LastPaymentValueDto value = lastPaymentValueService.getLastPaymentValueByCustomerAndProduct(customerId, productId);
        return ResponseEntity.ok(ApiResponse.success(value, "Last payment value retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'EMPLOYEE', 'ACCOUNTANT', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get last payment values by product", description = "Retrieve all last payment values for a specific product")
    public ResponseEntity<ApiResponse<List<LastPaymentValueDto>>> getLastPaymentValuesByProductId(
            @PathVariable UUID productId) {
        log.info("Fetching last payment values for product: {}", productId);
        List<LastPaymentValueDto> values = lastPaymentValueService.getLastPaymentValuesByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(values, "Last payment values retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}/search")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'EMPLOYEE', 'ACCOUNTANT', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Search last payment values by product name", description = "Search last payment values for a customer by product name")
    public ResponseEntity<ApiResponse<List<LastPaymentValueDto>>> searchByProductName(
            @PathVariable UUID customerId, @RequestParam String productName) {
        log.info("Searching last payment values for customer: {} with product name: {}", customerId, productName);
        List<LastPaymentValueDto> values = lastPaymentValueService.searchByProductName(customerId, productName);
        return ResponseEntity.ok(ApiResponse.success(values, "Last payment values searched successfully"));
    }

    @DeleteMapping("/customer/{customerId}/product/{productId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Delete last payment value", description = "Delete the last payment value for a specific customer and product")
    public ResponseEntity<ApiResponse<Void>> deleteLastPaymentValue(
            @PathVariable UUID customerId, @PathVariable UUID productId) {
        log.info("Deleting last payment value for customer: {} and product: {}", customerId, productId);
        lastPaymentValueService.deleteLastPaymentValue(customerId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Last payment value deleted successfully"));
    }
}
