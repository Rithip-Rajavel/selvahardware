package com.hardware.selvahardware.dto.employee;

import com.hardware.selvahardware.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class AttendanceDto {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String employeeCode;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String notes;
    private LocalDateTime createdAt;
}
