package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.invoice.*;
import com.hardware.selvahardware.enums.BillType;
import com.hardware.selvahardware.enums.InvoiceStatus;
import com.hardware.selvahardware.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice & Billing")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "Get all invoices with optional filters")
    public ResponseEntity<ApiResponse<List<InvoiceDto>>> getAllInvoices(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) BillType billType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.getInvoices(customerId, status, billType, fromDate, toDate, search)));
    }

    @PostMapping
    @Operation(summary = "Create new invoice")
    public ResponseEntity<ApiResponse<InvoiceDto>> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(invoiceService.createInvoice(request), "Invoice created"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get invoice statistics")
    public ResponseEntity<ApiResponse<InvoiceStatsDto>> getStats(
            @RequestParam(defaultValue = "month") String period) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getStats(period)));
    }

    @GetMapping("/next-number")
    @Operation(summary = "Get next invoice number")
    public ResponseEntity<ApiResponse<String>> nextNumber(
            @RequestParam(defaultValue = "INV") String prefix) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getNextInvoiceNumber(prefix)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getInvoiceById(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel/delete invoice")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable String id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Invoice deleted"));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Apply payment to invoice")
    public ResponseEntity<ApiResponse<InvoiceDto>> payInvoice(@PathVariable String id,
                                                               @Valid @RequestBody PayInvoiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.payInvoice(id, request)));
    }
}
