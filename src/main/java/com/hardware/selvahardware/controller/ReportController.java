package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @Operation(summary = "Get sales report")
    public ResponseEntity<ApiResponse<ReportService.SalesReport>> getSalesReport(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSalesReport(period, customerId, category)));
    }

    @GetMapping("/ledger/{customerId}")
    @Operation(summary = "Get customer ledger")
    public ResponseEntity<ApiResponse<ReportService.LedgerReport>> getLedger(@PathVariable String customerId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getLedger(customerId)));
    }

    @GetMapping("/inventory")
    @Operation(summary = "Get inventory report")
    public ResponseEntity<ApiResponse<ReportService.InventoryReport>> getInventory(
            @RequestParam(defaultValue = "false") boolean lowStock,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getInventoryReport(lowStock, category)));
    }

    @GetMapping("/commissions")
    @Operation(summary = "Get commission report")
    public ResponseEntity<ApiResponse<ReportService.CommissionReport>> getCommissions(
            @RequestParam(required = false) String contractorId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getCommissionReport(contractorId, fromDate, toDate)));
    }
}
