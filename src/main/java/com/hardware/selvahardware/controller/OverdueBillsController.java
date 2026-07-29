package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.invoice.OverdueBillDto;
import com.hardware.selvahardware.dto.invoice.OverdueSummaryDto;
import com.hardware.selvahardware.service.OverdueBillsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/overdue-bills")
@RequiredArgsConstructor
@Tag(name = "Overdue Bills")
@SecurityRequirement(name = "bearerAuth")
public class OverdueBillsController {

    private final OverdueBillsService overdueBillsService;

    /**
     * Get all overdue bills
     */
    @GetMapping
    @Operation(summary = "Get all overdue bills",
            description = "Returns all invoices that are past their due date with complete details")
    public ResponseEntity<ApiResponse<List<OverdueBillDto>>> getAllOverdueBills() {
        return ResponseEntity.ok(ApiResponse.success(overdueBillsService.getAllOverdueBills()));
    }

    /**
     * Get overdue bills summary for dashboard
     */
    @GetMapping("/summary")
    @Operation(summary = "Get overdue bills summary",
            description = "Returns summary statistics for overdue bills including aging buckets and top overdue customers")
    public ResponseEntity<ApiResponse<OverdueSummaryDto>> getOverdueSummary() {
        return ResponseEntity.ok(ApiResponse.success(overdueBillsService.getOverdueSummary()));
    }

    /**
     * Get overdue bills for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get overdue bills by customer",
            description = "Returns all overdue invoices for a specific customer")
    public ResponseEntity<ApiResponse<List<OverdueBillDto>>> getOverdueBillsByCustomer(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(
                overdueBillsService.getOverdueBillsByCustomer(customerId)));
    }

    /**
     * Check and mark an invoice as overdue
     */
    @PostMapping("/check/{invoiceId}")
    @Operation(summary = "Check and mark invoice as overdue",
            description = "Manually triggers overdue check for a specific invoice and logs it if overdue")
    public ResponseEntity<ApiResponse<Void>> checkAndMarkOverdue(@PathVariable UUID invoiceId) {
        overdueBillsService.checkAndMarkOverdue(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Overdue check completed"));
    }
}
