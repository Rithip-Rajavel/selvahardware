package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.employee.PayrollSummaryDto;
import com.hardware.selvahardware.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll Management")
@SecurityRequirement(name = "bearerAuth")
public class PayrollController {

    private final AttendanceService attendanceService;

    @GetMapping("/{employeeId}/summary")
    @Operation(summary = "Get payroll summary for an employee")
    public ResponseEntity<ApiResponse<PayrollSummaryDto>> getPayrollSummary(
            @PathVariable UUID employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.calculatePayroll(employeeId, year, month)));
    }
}
