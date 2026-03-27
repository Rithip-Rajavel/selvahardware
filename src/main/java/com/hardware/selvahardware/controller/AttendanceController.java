package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.employee.AttendanceDto;
import com.hardware.selvahardware.dto.employee.AttendanceRequest;
import com.hardware.selvahardware.dto.employee.PayrollSummaryDto;
import com.hardware.selvahardware.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance & Payroll")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @Operation(summary = "Mark or update attendance for an employee (upsert by employee+date)")
    public ResponseEntity<ApiResponse<AttendanceDto>> markAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.markAttendance(request), "Attendance marked"));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get attendance records for an employee in a date range")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getEmployeeAttendance(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getEmployeeAttendance(employeeId, from, to)));
    }

    @GetMapping("/daily")
    @Operation(summary = "Get all employees' attendance for a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceDto>>> getDailyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getDailyAttendance(targetDate)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attendance record (for corrections)")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable UUID id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Attendance record deleted"));
    }

    @GetMapping("/payroll/{employeeId}")
    @Operation(summary = "Calculate payroll with Loss of Pay for an employee (year+month)")
    public ResponseEntity<ApiResponse<PayrollSummaryDto>> getPayroll(
            @PathVariable UUID employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.calculatePayroll(employeeId, year, month)));
    }
}
