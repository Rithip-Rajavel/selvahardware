package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.pricing.PriceMappingDto;
import com.hardware.selvahardware.dto.pricing.PriceMappingRequest;
import com.hardware.selvahardware.service.PriceMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Tag(name = "Price Mapping")
@SecurityRequirement(name = "bearerAuth")
public class PricingController {

    private final PriceMappingService priceMappingService;

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer-specific pricing")
    public ResponseEntity<ApiResponse<List<PriceMappingDto>>> getByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(priceMappingService.getByCustomer(customerId)));
    }

    @PostMapping
    @Operation(summary = "Create or update price mapping")
    public ResponseEntity<ApiResponse<PriceMappingDto>> createOrUpdate(@Valid @RequestBody PriceMappingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(priceMappingService.createOrUpdate(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing price mapping")
    public ResponseEntity<ApiResponse<PriceMappingDto>> update(@PathVariable UUID id,
                                                                @Valid @RequestBody PriceMappingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(priceMappingService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete price mapping")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        priceMappingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Price mapping deleted"));
    }
}
