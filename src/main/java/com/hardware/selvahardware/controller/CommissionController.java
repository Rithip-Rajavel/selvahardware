package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.commission.CommissionDto;
import com.hardware.selvahardware.dto.commission.CommissionRequest;
import com.hardware.selvahardware.service.CommissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
@Tag(name = "Commission Management")
@SecurityRequirement(name = "bearerAuth")
public class CommissionController {

    private final CommissionService commissionService;

    @GetMapping
    @Operation(summary = "Get all commissions with optional filters")
    public ResponseEntity<ApiResponse<List<CommissionDto>>> getAll(
            @RequestParam(required = false) String contractorId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(ApiResponse.success(commissionService.getAll(contractorId, fromDate, toDate)));
    }

    @PostMapping
    @Operation(summary = "Add commission record")
    public ResponseEntity<ApiResponse<CommissionDto>> create(@Valid @RequestBody CommissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(commissionService.create(request), "Commission recorded"));
    }

    @GetMapping("/contractor/{contractorId}")
    @Operation(summary = "Get contractor commission history")
    public ResponseEntity<ApiResponse<List<CommissionDto>>> getByContractor(@PathVariable String contractorId) {
        return ResponseEntity.ok(ApiResponse.success(commissionService.getByContractor(contractorId)));
    }

    @GetMapping("/contractor/{contractorId}/total")
    @Operation(summary = "Get total commission for contractor")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getTotal(@PathVariable String contractorId) {
        BigDecimal total = commissionService.getTotalByContractor(contractorId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("total", total)));
    }
}
