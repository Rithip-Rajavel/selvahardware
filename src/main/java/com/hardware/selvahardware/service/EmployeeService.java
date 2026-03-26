package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.employee.EmployeeDto;
import com.hardware.selvahardware.dto.employee.EmployeeRequest;
import com.hardware.selvahardware.entity.Employee;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.EmployeeMapper;
import com.hardware.selvahardware.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeDto> getAllEmployees(Boolean active, String search) {
        return employeeRepository.searchEmployees(active, search)
                .stream().map(employeeMapper::toDto).collect(Collectors.toList());
    }

    public EmployeeDto getById(String id) {
        return employeeMapper.toDto(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id)));
    }

    public EmployeeDto create(EmployeeRequest request) {
        String id = UUID.randomUUID().toString();
        String code = (request.getCode() != null && !request.getCode().isBlank())
                ? request.getCode().toUpperCase()
                : generateCode();
        if (employeeRepository.existsByCode(code)) {
            throw new BusinessException("Employee code already exists: " + code);
        }
        Employee employee = employeeMapper.toEntity(request, id, code);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    public EmployeeDto update(String id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        employeeMapper.updateEntity(employee, request);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    public void delete(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        // Soft delete - just mark inactive
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    private String generateCode() {
        long count = employeeRepository.count() + 1;
        return String.format("EMP%03d", count);
    }
}
