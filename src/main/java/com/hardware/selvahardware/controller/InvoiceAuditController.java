package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.invoice.InvoiceAuditLogDto;
import com.hardware.selvahardware.dto.invoice.InvoiceAuditTrailDto;
import com.hardware.selvahardware.service.InvoiceAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-audit")
@RequiredArgsConstructor
@Tag(name = "Invoice Audit Trail")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceAuditController {

    private final InvoiceAuditService invoiceAuditService;

    /**
     * Get complete audit trail for an invoice
     */
    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get invoice audit trail",
            description = "Returns complete transaction history for an invoice including all payments, " +
                         "returns, status changes, and overdue events")
    public ResponseEntity<ApiResponse<InvoiceAuditTrailDto>> getInvoiceAuditTrail(
            @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceAuditService.getInvoiceAuditTrail(invoiceId)));
    }

    /**
     * Get audit logs for a customer
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer audit logs",
            description = "Returns all audit log entries for a specific customer across all their invoices")
    public ResponseEntity<ApiResponse<List<InvoiceAuditLogDto>>> getCustomerAuditLogs(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceAuditService.getCustomerAuditLogs(customerId)));
    }

    /**
     * Get all overdue payment logs
     */
    @GetMapping("/overdue-payments")
    @Operation(summary = "Get overdue payment logs",
            description = "Returns all overdue payment audit log entries")
    public ResponseEntity<ApiResponse<List<InvoiceAuditLogDto>>> getOverduePaymentLogs() {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceAuditService.getOverduePaymentLogs()));
    }
}
