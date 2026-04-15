package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.quotation.QuotationDto;
import com.hardware.selvahardware.dto.quotation.QuotationRequest;
import com.hardware.selvahardware.enums.QuotationStatus;
import com.hardware.selvahardware.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
@Tag(name = "Quotation Management")
@SecurityRequirement(name = "bearerAuth")
public class QuotationController {

    private final QuotationService quotationService;

    @GetMapping
    @Operation(summary = "Get all quotations")
    public ResponseEntity<ApiResponse<List<QuotationDto>>> getAllQuotations() {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getAllQuotations()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quotation by ID")
    public ResponseEntity<ApiResponse<QuotationDto>> getQuotationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getQuotationById(id)));
    }

    @PostMapping
    @Operation(summary = "Create new quotation")
    public ResponseEntity<ApiResponse<QuotationDto>> createQuotation(@Valid @RequestBody QuotationRequest request) {
        QuotationDto created = quotationService.createQuotation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Quotation created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quotation")
    public ResponseEntity<ApiResponse<QuotationDto>> updateQuotation(@PathVariable UUID id,
            @Valid @RequestBody QuotationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(quotationService.updateQuotation(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quotation")
    public ResponseEntity<ApiResponse<Void>> deleteQuotation(@PathVariable UUID id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Quotation deleted successfully"));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm quotation")
    public ResponseEntity<ApiResponse<QuotationDto>> confirmQuotation(@PathVariable UUID id) {
        return ResponseEntity
                .ok(ApiResponse.success(quotationService.confirmQuotation(id), "Quotation confirmed successfully"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel quotation")
    public ResponseEntity<ApiResponse<QuotationDto>> cancelQuotation(@PathVariable UUID id) {
        return ResponseEntity
                .ok(ApiResponse.success(quotationService.cancelQuotation(id), "Quotation cancelled successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get quotations by customer")
    public ResponseEntity<ApiResponse<List<QuotationDto>>> getQuotationsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getQuotationsByCustomer(customerId)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get quotations by status")
    public ResponseEntity<ApiResponse<List<QuotationDto>>> getQuotationsByStatus(@PathVariable QuotationStatus status) {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getQuotationsByStatus(status)));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter quotations")
    public ResponseEntity<ApiResponse<List<QuotationDto>>> filterQuotations(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) QuotationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
                ApiResponse.success(quotationService.filterQuotations(customerId, status, fromDate, toDate, search)));
    }

    @GetMapping("/expired")
    @Operation(summary = "Get expired quotations")
    public ResponseEntity<ApiResponse<List<QuotationDto>>> getExpiredQuotations() {
        return ResponseEntity.ok(ApiResponse.success(quotationService.getExpiredQuotations()));
    }

    @PostMapping("/{id}/convert-to-invoice")
    @Operation(summary = "Convert quotation to invoice")
    public ResponseEntity<ApiResponse<com.hardware.selvahardware.dto.invoice.InvoiceDto>> convertToInvoice(
            @PathVariable UUID id,
            @RequestParam(required = false) java.math.BigDecimal amountPaid) {
        return ResponseEntity.ok(ApiResponse.success(quotationService.convertToInvoice(id, amountPaid),
                "Quotation converted to invoice successfully"));
    }
}
