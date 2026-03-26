package com.hardware.selvahardware.dto.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRequest {

    @NotBlank
    private String name;

    private String code; // auto-generated if blank

    private String phone;
    private String email;
    private String designation;
    private String department;
    private LocalDate dateOfJoining;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monthlySalary;

    private boolean isActive = true;
}
