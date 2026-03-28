package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.bonus.BonusDto;
import com.hardware.selvahardware.dto.bonus.BonusRequest;
import com.hardware.selvahardware.service.BonusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bonuses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bonus Management", description = "APIs for managing customer bonuses")
public class BonusController {

    private final BonusService bonusService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Create a new bonus", description = "Add a bonus to a customer with reason, month/year, and amount")
    public ResponseEntity<ApiResponse<BonusDto>> createBonus(@Valid @RequestBody BonusRequest request) {
        log.info("Creating bonus for customer: {}", request.getCustomerId());
        BonusDto bonus = bonusService.createBonus(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bonus, "Bonus created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ACCOUNTANT', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get all bonuses", description = "Retrieve all bonuses in the system")
    public ResponseEntity<ApiResponse<List<BonusDto>>> getAllBonuses() {
        log.info("Fetching all bonuses");
        List<BonusDto> bonuses = bonusService.getAllBonuses();
        return ResponseEntity.ok(ApiResponse.success(bonuses, "All bonuses retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ACCOUNTANT', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get bonuses by customer", description = "Retrieve all bonuses for a specific customer")
    public ResponseEntity<ApiResponse<List<BonusDto>>> getBonusesByCustomerId(@PathVariable UUID customerId) {
        log.info("Fetching bonuses for customer: {}", customerId);
        List<BonusDto> bonuses = bonusService.getBonusesByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(bonuses, "Bonuses retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}/year/{year}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ACCOUNTANT', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get bonuses by customer and year", description = "Retrieve all bonuses for a specific customer in a given year")
    public ResponseEntity<ApiResponse<List<BonusDto>>> getBonusesByCustomerAndYear(
            @PathVariable UUID customerId, @PathVariable Integer year) {
        log.info("Fetching bonuses for customer: {} for year: {}", customerId, year);
        List<BonusDto> bonuses = bonusService.getBonusesByCustomerAndYear(customerId, year);
        return ResponseEntity.ok(ApiResponse.success(bonuses, "Bonuses retrieved successfully"));
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ACCOUNTANT', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get bonuses by month and year", description = "Retrieve all bonuses for a specific month and year")
    public ResponseEntity<ApiResponse<List<BonusDto>>> getBonusesByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        log.info("Fetching bonuses for month: {} and year: {}", month, year);
        List<BonusDto> bonuses = bonusService.getBonusesByMonthAndYear(month, year);
        return ResponseEntity.ok(ApiResponse.success(bonuses, "Bonuses retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}/year/{year}/total")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ACCOUNTANT', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get total bonus for customer by year", description = "Calculate total bonus amount for a customer in a given year")
    public ResponseEntity<ApiResponse<Double>> getTotalBonusForCustomerByYear(
            @PathVariable UUID customerId, @PathVariable Integer year) {
        log.info("Calculating total bonus for customer: {} for year: {}", customerId, year);
        Double totalBonus = bonusService.getTotalBonusForCustomerByYear(customerId, year);
        return ResponseEntity.ok(ApiResponse.success(totalBonus, "Total bonus calculated successfully"));
    }

    @GetMapping("/customer/{customerId}/month/{month}/year/{year}/total")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'ACCOUNTANT', 'EMPLOYEE', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Get total bonus for customer by month and year", description = "Calculate total bonus amount for a customer in a given month and year")
    public ResponseEntity<ApiResponse<Double>> getTotalBonusForCustomerByMonthAndYear(
            @PathVariable UUID customerId, @PathVariable Integer month, @PathVariable Integer year) {
        log.info("Calculating total bonus for customer: {} for month: {} and year: {}", customerId, month, year);
        Double totalBonus = bonusService.getTotalBonusForCustomerByMonthAndYear(customerId, year, month);
        return ResponseEntity.ok(ApiResponse.success(totalBonus, "Total bonus calculated successfully"));
    }

    @PutMapping("/{bonusId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Update a bonus", description = "Update an existing bonus")
    public ResponseEntity<ApiResponse<BonusDto>> updateBonus(
            @PathVariable UUID bonusId, @Valid @RequestBody BonusRequest request) {
        log.info("Updating bonus with id: {}", bonusId);
        BonusDto bonus = bonusService.updateBonus(bonusId, request);
        return ResponseEntity.ok(ApiResponse.success(bonus, "Bonus updated successfully"));
    }

    @DeleteMapping("/{bonusId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Delete a bonus", description = "Delete an existing bonus")
    public ResponseEntity<ApiResponse<Void>> deleteBonus(@PathVariable UUID bonusId) {
        log.info("Deleting bonus with id: {}", bonusId);
        bonusService.deleteBonus(bonusId);
        return ResponseEntity.ok(ApiResponse.success(null, "Bonus deleted successfully"));
    }
}
