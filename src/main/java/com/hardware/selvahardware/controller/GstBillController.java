package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.gstbill.GstBillDto;
import com.hardware.selvahardware.dto.gstbill.GstBillRequest;
import com.hardware.selvahardware.service.GstBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gst-bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GstBillController {

    private final GstBillService gstBillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GstBillDto>>> getAllGstBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String toDate) {
        
        List<GstBillDto> gstBills;
        if (customerName != null || fromDate != null || toDate != null) {
            LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
            LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;
            gstBills = gstBillService.getGstBillsWithFilters(customerName, from, to);
        } else {
            gstBills = gstBillService.getAllGstBills();
        }
        
        return ResponseEntity.ok(ApiResponse.success(gstBills, "GST Bills retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GstBillDto>> getGstBillById(@PathVariable UUID id) {
        GstBillDto gstBill = gstBillService.getGstBillById(id);
        return ResponseEntity.ok(ApiResponse.success(gstBill, "GST Bill retrieved successfully"));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<GstBillDto>> getGstBillByInvoiceId(@PathVariable UUID invoiceId) {
        GstBillDto gstBill = gstBillService.getGstBillByInvoiceId(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(gstBill, "GST Bill retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GstBillDto>> createGstBill(@Valid @RequestBody GstBillRequest request) {
        GstBillDto createdGstBill = gstBillService.createGstBill(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdGstBill, "GST Bill created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GstBillDto>> updateGstBill(
            @PathVariable UUID id,
            @Valid @RequestBody GstBillRequest request) {
        GstBillDto updatedGstBill = gstBillService.updateGstBill(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedGstBill, "GST Bill updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGstBill(@PathVariable UUID id) {
        gstBillService.deleteGstBill(id);
        return ResponseEntity.ok(ApiResponse.success(null, "GST Bill deleted successfully"));
    }
}
