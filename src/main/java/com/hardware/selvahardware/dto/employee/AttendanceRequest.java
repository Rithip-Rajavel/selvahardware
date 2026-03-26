package com.hardware.selvahardware.dto.employee;

import com.hardware.selvahardware.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceRequest {

    @NotNull
    private String employeeId;

    @NotNull
    private LocalDate attendanceDate;

    @NotNull
    private AttendanceStatus status;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String notes;
}
