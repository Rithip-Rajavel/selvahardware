package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.employee.AttendanceDto;
import com.hardware.selvahardware.dto.employee.AttendanceRequest;
import com.hardware.selvahardware.dto.employee.PayrollSummaryDto;
import com.hardware.selvahardware.entity.Attendance;
import com.hardware.selvahardware.entity.Employee;
import com.hardware.selvahardware.enums.AttendanceStatus;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.EmployeeMapper;
import com.hardware.selvahardware.repository.AttendanceRepository;
import com.hardware.selvahardware.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    /**
     * Mark or update attendance for an employee on a given date.
     * If already exists, updates it (upsert behavior).
     */
    public AttendanceDto markAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(request.getEmployeeId(), request.getAttendanceDate())
                .orElse(Attendance.builder()
                        .id(UUID.randomUUID().toString())
                        .employee(employee)
                        .build());

        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setStatus(request.getStatus());
        attendance.setCheckInTime(request.getCheckInTime());
        attendance.setCheckOutTime(request.getCheckOutTime());
        attendance.setNotes(request.getNotes());

        return employeeMapper.toAttendanceDto(attendanceRepository.save(attendance));
    }

    /**
     * Get attendance records for a single employee within a date range.
     */
    public List<AttendanceDto> getEmployeeAttendance(String employeeId, LocalDate from, LocalDate to) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", employeeId);
        }
        return attendanceRepository
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(employeeId, from, to)
                .stream().map(employeeMapper::toAttendanceDto).collect(Collectors.toList());
    }

    /**
     * Get all employees' attendance for a specific date (for daily view).
     */
    public List<AttendanceDto> getDailyAttendance(LocalDate date) {
        return attendanceRepository
                .findByAttendanceDateBetweenOrderByAttendanceDateDesc(date, date)
                .stream().map(employeeMapper::toAttendanceDto).collect(Collectors.toList());
    }

    /**
     * Delete attendance record (correction).
     */
    public void deleteAttendance(String id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance", id);
        }
        attendanceRepository.deleteById(id);
    }

    /**
     * Calculate payroll for an employee for a given year-month.
     *
     * LOP (Loss of Pay) Logic:
     * - Total working days = calendar days in month (excluding Sundays by default, configurable)
     * - Per day salary = monthlySalary / totalWorkingDays
     * - LOP days = absentDays + (halfDays * 0.5)
     * - Net salary = monthlySalary - (LOP days * perDaySalary)
     */
    public PayrollSummaryDto calculatePayroll(String employeeId, int year, int month) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        // Count working days in the month (Mon-Sat, excluding Sundays)
        int totalWorkingDays = countWorkingDays(from, to);

        // Fetch all attendance records
        List<Attendance> records = attendanceRepository
                .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(employeeId, from, to);

        int presentDays = 0, absentDays = 0, halfDays = 0, leaveDays = 0, holidayDays = 0;

        for (Attendance a : records) {
            switch (a.getStatus()) {
                case present  -> presentDays++;
                case absent   -> absentDays++;
                case half_day -> halfDays++;
                case leave    -> leaveDays++;
                case holiday  -> holidayDays++;
            }
        }

        BigDecimal monthlySalary = employee.getMonthlySalary();
        BigDecimal workingDaysBD = BigDecimal.valueOf(totalWorkingDays);

        // Per day salary = monthly / working days
        BigDecimal perDaySalary = totalWorkingDays > 0
                ? monthlySalary.divide(workingDaysBD, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Loss of pay days: absent = full day, half_day = 0.5 day
        BigDecimal lopDays = BigDecimal.valueOf(absentDays)
                .add(BigDecimal.valueOf(halfDays).multiply(BigDecimal.valueOf(0.5)));

        BigDecimal lopAmount = lopDays.multiply(perDaySalary).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netSalary = monthlySalary.subtract(lopAmount).max(BigDecimal.ZERO);

        List<AttendanceDto> attendanceDtos = records.stream()
                .map(employeeMapper::toAttendanceDto).collect(Collectors.toList());

        return PayrollSummaryDto.builder()
                .employeeId(employeeId)
                .employeeName(employee.getName())
                .employeeCode(employee.getCode())
                .year(year)
                .month(month)
                .totalWorkingDays(totalWorkingDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .halfDays(halfDays)
                .leaveDays(leaveDays)
                .holidayDays(holidayDays)
                .monthlySalary(monthlySalary)
                .perDaySalary(perDaySalary.setScale(2, RoundingMode.HALF_UP))
                .lossOfPayDays(lopDays)
                .lossOfPayAmount(lopAmount)
                .netPayableSalary(netSalary)
                .attendanceDetails(attendanceDtos)
                .build();
    }

    /**
     * Count Monday–Saturday working days in a given range.
     */
    private int countWorkingDays(LocalDate from, LocalDate to) {
        int count = 0;
        LocalDate current = from;
        while (!current.isAfter(to)) {
            if (current.getDayOfWeek().getValue() != 7) { // 7 = Sunday
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }
}
