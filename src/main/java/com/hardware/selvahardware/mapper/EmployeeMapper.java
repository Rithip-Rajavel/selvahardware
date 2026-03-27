package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.employee.AttendanceDto;
import com.hardware.selvahardware.dto.employee.EmployeeDto;
import com.hardware.selvahardware.dto.employee.EmployeeRequest;
import com.hardware.selvahardware.entity.Attendance;
import com.hardware.selvahardware.entity.Employee;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDto toDto(Employee e) {
        return EmployeeDto.builder()
                .id(e.getId())
                .name(e.getName())
                .code(e.getCode())
                .phone(e.getPhone())
                .email(e.getEmail())
                .designation(e.getDesignation())
                .department(e.getDepartment())
                .dateOfJoining(e.getDateOfJoining())
                .monthlySalary(e.getMonthlySalary())
                .isActive(e.isActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public Employee toEntity(EmployeeRequest req, UUID id, String code) {
        return Employee.builder()
                .name(req.getName())
                .code(code)
                .phone(req.getPhone())
                .email(req.getEmail())
                .designation(req.getDesignation())
                .department(req.getDepartment())
                .dateOfJoining(req.getDateOfJoining())
                .monthlySalary(req.getMonthlySalary())
                .isActive(req.isActive())
                .build();
    }

    public void updateEntity(Employee e, EmployeeRequest req) {
        e.setName(req.getName());
        e.setPhone(req.getPhone());
        e.setEmail(req.getEmail());
        e.setDesignation(req.getDesignation());
        e.setDepartment(req.getDepartment());
        e.setDateOfJoining(req.getDateOfJoining());
        e.setMonthlySalary(req.getMonthlySalary());
        e.setActive(req.isActive());
    }

    public AttendanceDto toAttendanceDto(Attendance a) {
        return AttendanceDto.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getId())
                .employeeName(a.getEmployee().getName())
                .employeeCode(a.getEmployee().getCode())
                .attendanceDate(a.getAttendanceDate())
                .status(a.getStatus())
                .checkInTime(a.getCheckInTime())
                .checkOutTime(a.getCheckOutTime())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
