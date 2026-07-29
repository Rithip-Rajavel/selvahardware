package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.contractor.ContractorPendingBillsDto;
import com.hardware.selvahardware.service.ContractorPendingBillsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contractors")
@RequiredArgsConstructor
@Tag(name = "Contractor Pending Bills")
@SecurityRequirement(name = "bearerAuth")
public class ContractorPendingBillsController {

    private final ContractorPendingBillsService contractorPendingBillsService;

    /**
     * Get complete pending bills information for a specific contractor
     * including all linked customers and their pending bills
     */
    @GetMapping("/{contractorId}/pending-bills")
    @Operation(summary = "Get contractor pending bills with linked customer details",
            description = "Returns complete pending bills information for a contractor including all linked customers and their pending bills with full details")
    public ResponseEntity<ApiResponse<ContractorPendingBillsDto>> getContractorPendingBills(
            @PathVariable UUID contractorId) {
        return ResponseEntity.ok(ApiResponse.success(
                contractorPendingBillsService.getContractorPendingBills(contractorId)));
    }

    /**
     * Get pending bills summary for all contractors
     */
    @GetMapping("/pending-bills")
    @Operation(summary = "Get all contractors pending bills summary",
            description = "Returns pending bills summary for all contractors with their linked customers")
    public ResponseEntity<ApiResponse<List<ContractorPendingBillsDto>>> getAllContractorsPendingBills() {
        return ResponseEntity.ok(ApiResponse.success(
                contractorPendingBillsService.getAllContractorsPendingBills()));
    }
}
