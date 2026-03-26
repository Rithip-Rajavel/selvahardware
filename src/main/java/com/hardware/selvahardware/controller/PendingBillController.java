package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.pending.PendingBillDto;
import com.hardware.selvahardware.dto.pending.PendingBillRequest;
import com.hardware.selvahardware.service.PendingBillService;
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
@RequestMapping("/api/pending-bills")
@RequiredArgsConstructor
@Tag(name = "Pending Bills (Rough Drafts)")
@SecurityRequirement(name = "bearerAuth")
public class PendingBillController {

    private final PendingBillService pendingBillService;

    @GetMapping
    @Operation(summary = "Get all pending bills")
    public ResponseEntity<ApiResponse<List<PendingBillDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(pendingBillService.getAll()));
    }

    @PostMapping
    @Operation(summary = "Save pending bill")
    public ResponseEntity<ApiResponse<PendingBillDto>> create(@Valid @RequestBody PendingBillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(pendingBillService.create(request), "Pending bill saved"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pending bill by ID")
    public ResponseEntity<ApiResponse<PendingBillDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(pendingBillService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pending bill")
    public ResponseEntity<ApiResponse<PendingBillDto>> update(@PathVariable String id,
                                                               @Valid @RequestBody PendingBillRequest request) {
        return ResponseEntity.ok(ApiResponse.success(pendingBillService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pending bill")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        pendingBillService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pending bill deleted"));
    }
}
