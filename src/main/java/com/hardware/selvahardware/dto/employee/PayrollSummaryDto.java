package com.hardware.selvahardware.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PayrollSummaryDto {
    private UUID employeeId;
    private String employeeName;
    private String employeeCode;
    private int year;
    private int month;
    private int totalWorkingDays;   // calendar working days in month
    private int presentDays;
    private int absentDays;
    private int halfDays;
    private int leaveDays;
    private int holidayDays;
    private BigDecimal monthlySalary;
    private BigDecimal perDaySalary;
    // Loss of pay = absent days * perDaySalary + halfDays * perDaySalary * 0.5
    private BigDecimal lossOfPayDays;   // effective days deducted (absent + 0.5*half_day)
    private BigDecimal lossOfPayAmount;
    private BigDecimal netPayableSalary;
    private List<AttendanceDto> attendanceDetails;
}
