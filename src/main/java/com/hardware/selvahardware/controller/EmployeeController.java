package com.hardware.selvahardware.controller;

import com.hardware.selvahardware.dto.ApiResponse;
import com.hardware.selvahardware.dto.employee.EmployeeDto;
import com.hardware.selvahardware.dto.employee.EmployeeRequest;
import com.hardware.selvahardware.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees with optional active filter and search")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAllEmployees(active, search)));
    }

    @PostMapping
    @Operation(summary = "Add new employee (code auto-generated if not provided)")
    public ResponseEntity<ApiResponse<EmployeeDto>> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employeeService.create(request), "Employee added"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee details or salary")
    public ResponseEntity<ApiResponse<EmployeeDto>> update(@PathVariable UUID id,
                                                            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate employee (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee deactivated"));
    }
}
