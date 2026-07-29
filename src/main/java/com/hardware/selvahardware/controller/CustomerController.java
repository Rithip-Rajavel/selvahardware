package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.customer.CustomerBalanceDto;
import com.hardware.selvahardware.dto.customer.CustomerDto;
import com.hardware.selvahardware.dto.customer.CustomerRequest;
import com.hardware.selvahardware.enums.CustomerType;
import com.hardware.selvahardware.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers with optional filter and search")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers(
            @RequestParam(required = false) CustomerType type,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getAllCustomers(type, search)));
    }

    @PostMapping
    @Operation(summary = "Add new customer")
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(customerService.createCustomer(request), "Customer created"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ApiResponse<CustomerDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomerById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer details")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(@PathVariable UUID id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(customerService.updateCustomer(id, request), "Customer updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Customer deleted"));
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Get customer balance summary")
    public ResponseEntity<ApiResponse<CustomerBalanceDto>> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomerBalance(id)));
    }

    @GetMapping("/contractor/{contractorId}")
    @Operation(summary = "Get all customers linked to a contractor")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getCustomersByContractor(@PathVariable UUID contractorId) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomersByContractor(contractorId)));
    }
}
