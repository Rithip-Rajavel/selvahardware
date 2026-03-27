package com.hardware.selvahardware.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmployeeDto {
    private UUID id;
    private String name;
    private String code;
    private String phone;
    private String email;
    private String designation;
    private String department;
    private LocalDate dateOfJoining;
    private BigDecimal monthlySalary;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
