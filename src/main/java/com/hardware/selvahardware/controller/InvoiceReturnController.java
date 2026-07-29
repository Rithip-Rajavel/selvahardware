package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.invoice.InvoiceReturnDto;
import com.hardware.selvahardware.dto.invoice.InvoiceReturnRequest;
import com.hardware.selvahardware.service.InvoiceReturnService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-returns")
@RequiredArgsConstructor
@Tag(name = "Invoice Returns (Written Bills)")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceReturnController {

        private final InvoiceReturnService invoiceReturnService;

        /**
         * Get all returns
         */
        @GetMapping
        @Operation(summary = "Get all returns", description = "Returns all return/written bill records")
        public ResponseEntity<ApiResponse<List<InvoiceReturnDto>>> getAllReturns() {
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.getAllReturns()));
        }

        /**
         * Get all returns for an invoice
         */
        @GetMapping("/invoice/{invoiceId}")
        @Operation(summary = "Get all returns for an invoice", description = "Returns all return/written bill records for a specific invoice")
        public ResponseEntity<ApiResponse<List<InvoiceReturnDto>>> getReturnsByInvoice(
                        @PathVariable UUID invoiceId) {
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.getReturnsByInvoice(invoiceId)));
        }

        /**
         * Get return by ID
         */
        @GetMapping("/{returnId}")
        @Operation(summary = "Get return by ID", description = "Returns details of a specific return")
        public ResponseEntity<ApiResponse<InvoiceReturnDto>> getReturnById(
                        @PathVariable UUID returnId) {
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.getReturnById(returnId)));
        }

        /**
         * Get returns for a customer
         */
        @GetMapping("/customer/{customerId}")
        @Operation(summary = "Get returns by customer", description = "Returns all return records for a specific customer")
        public ResponseEntity<ApiResponse<List<InvoiceReturnDto>>> getReturnsByCustomer(
                        @PathVariable UUID customerId) {
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.getReturnsByCustomer(customerId)));
        }

        /**
         * Create a new return request
         */
        @PostMapping
        @Operation(summary = "Create return request", description = "Creates a new return/written bill request for items from an invoice")
        public ResponseEntity<ApiResponse<InvoiceReturnDto>> createReturn(
                        @Valid @RequestBody InvoiceReturnRequest request,
                        @RequestParam(required = false) String processedBy) {
                String processor = processedBy != null ? processedBy : "system";
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(
                                                invoiceReturnService.createReturn(request, processor),
                                                "Return request created"));
        }

        /**
         * Process a return - updates stock and invoice balance
         */
        @PostMapping("/{returnId}/process")
        @Operation(summary = "Process return", description = "Processes the return, updates stock, and adjusts invoice balance")
        public ResponseEntity<ApiResponse<InvoiceReturnDto>> processReturn(
                        @PathVariable UUID returnId,
                        @RequestParam(required = false) String refundMethod,
                        @RequestParam(required = false) String processedBy) {
                String processor = processedBy != null ? processedBy : "system";
                String method = refundMethod != null ? refundMethod : "cash";
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.processReturn(returnId, method, processor),
                                "Return processed successfully"));
        }

        /**
         * Refund a processed return
         */
        @PostMapping("/{returnId}/refund")
        @Operation(summary = "Refund return", description = "Completes refund for a processed return")
        public ResponseEntity<ApiResponse<InvoiceReturnDto>> refundReturn(
                        @PathVariable UUID returnId,
                        @RequestParam(required = false) String refundMethod,
                        @RequestParam(required = false) String processedBy) {
                String processor = processedBy != null ? processedBy : "system";
                String method = refundMethod != null ? refundMethod : "cash";
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.refundReturn(returnId, method, processor),
                                "Refund completed"));
        }

        /**
         * Cancel a return
         */
        @DeleteMapping("/{returnId}")
        @Operation(summary = "Cancel return", description = "Cancels a pending return request")
        public ResponseEntity<ApiResponse<Void>> cancelReturn(@PathVariable UUID returnId) {
                invoiceReturnService.cancelReturn(returnId);
                return ResponseEntity.ok(ApiResponse.success(null, "Return cancelled"));
        }

        /**
         * Get total returned amount for a customer
         */
        @GetMapping("/customer/{customerId}/total-returned")
        @Operation(summary = "Get total returned amount by customer", description = "Returns the total amount of all processed returns for a customer")
        public ResponseEntity<ApiResponse<BigDecimal>> getTotalReturnedByCustomer(
                        @PathVariable UUID customerId) {
                return ResponseEntity.ok(ApiResponse.success(
                                invoiceReturnService.getTotalReturnedAmountByCustomer(customerId)));
        }
}
