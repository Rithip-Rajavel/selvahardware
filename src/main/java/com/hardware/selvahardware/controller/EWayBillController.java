package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.ewaybill.EWayBillDto;
import com.hardware.selvahardware.dto.ewaybill.EWayBillRequest;
import com.hardware.selvahardware.enums.EWayBillStatus;
import com.hardware.selvahardware.service.EWayBillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eway-bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EWayBillController {

    private final EWayBillService ewayBillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EWayBillDto>>> getAllEWayBills() {
        List<EWayBillDto> ewayBills = ewayBillService.getAllEWayBills();
        return ResponseEntity.ok(ApiResponse.success(ewayBills, "E-Way Bills retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EWayBillDto>> getEWayBillById(@PathVariable UUID id) {
        EWayBillDto ewayBill = ewayBillService.getEWayBillById(id);
        return ResponseEntity.ok(ApiResponse.success(ewayBill, "E-Way Bill retrieved successfully"));
    }

    @GetMapping("/number/{ewayBillNo}")
    public ResponseEntity<ApiResponse<EWayBillDto>> getEWayBillByNumber(@PathVariable String ewayBillNo) {
        EWayBillDto ewayBill = ewayBillService.getEWayBillByNumber(ewayBillNo);
        return ResponseEntity.ok(ApiResponse.success(ewayBill, "E-Way Bill retrieved successfully"));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<List<EWayBillDto>>> getEWayBillsByInvoiceId(@PathVariable UUID invoiceId) {
        List<EWayBillDto> ewayBills = ewayBillService.getEWayBillsByInvoiceId(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(ewayBills, "E-Way Bills for invoice retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<EWayBillDto>>> getEWayBillsByStatus(@PathVariable EWayBillStatus status) {
        List<EWayBillDto> ewayBills = ewayBillService.getEWayBillsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(ewayBills, "E-Way Bills by status retrieved successfully"));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<EWayBillDto>>> getEWayBillsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String toDate) {
        List<EWayBillDto> ewayBills = ewayBillService.getEWayBillsByDateRange(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(ewayBills, "E-Way Bills by date range retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EWayBillDto>> createEWayBill(@Valid @RequestBody EWayBillRequest request) {
        EWayBillDto createdEWayBill = ewayBillService.createEWayBill(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdEWayBill, "E-Way Bill created successfully"));
    }

    @PostMapping("/from-invoice/{invoiceId}")
    public ResponseEntity<ApiResponse<EWayBillDto>> createEWayBillFromInvoice(@PathVariable UUID invoiceId) {
        EWayBillDto createdEWayBill = ewayBillService.createEWayBillFromInvoice(invoiceId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdEWayBill, "E-Way Bill created from invoice successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EWayBillDto>> updateEWayBill(
            @PathVariable UUID id,
            @Valid @RequestBody EWayBillRequest request) {
        EWayBillDto updatedEWayBill = ewayBillService.updateEWayBill(id, request);
        return ResponseEntity.ok(ApiResponse.success(updatedEWayBill, "E-Way Bill updated successfully"));
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<EWayBillDto>> generateEWayBill(@PathVariable UUID id) {
        EWayBillDto generatedEWayBill = ewayBillService.generateEWayBill(id);
        return ResponseEntity.ok(ApiResponse.success(generatedEWayBill, "E-Way Bill generated successfully"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<EWayBillDto>> cancelEWayBill(@PathVariable UUID id) {
        EWayBillDto cancelledEWayBill = ewayBillService.cancelEWayBill(id);
        return ResponseEntity.ok(ApiResponse.success(cancelledEWayBill, "E-Way Bill cancelled successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEWayBill(@PathVariable UUID id) {
        ewayBillService.deleteEWayBill(id);
        return ResponseEntity.ok(ApiResponse.success(null, "E-Way Bill deleted successfully"));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> getEWayBillCountByStatus(@PathVariable EWayBillStatus status) {
        long count = ewayBillService.getEWayBillCountByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count, "E-Way Bill count retrieved successfully"));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<java.util.Map<String, Long>>> getEWayBillStatistics() {
        long draftCount = ewayBillService.getEWayBillCountByStatus(EWayBillStatus.draft);
        long generatedCount = ewayBillService.getEWayBillCountByStatus(EWayBillStatus.generated);
        long cancelledCount = ewayBillService.getEWayBillCountByStatus(EWayBillStatus.cancelled);

        java.util.Map<String, Long> statistics = new java.util.HashMap<>();
        statistics.put("draft", draftCount);
        statistics.put("generated", generatedCount);
        statistics.put("cancelled", cancelledCount);
        statistics.put("total", draftCount + generatedCount + cancelledCount);

        return ResponseEntity.ok(ApiResponse.success(statistics, "E-Way Bill statistics retrieved successfully"));
    }
}
