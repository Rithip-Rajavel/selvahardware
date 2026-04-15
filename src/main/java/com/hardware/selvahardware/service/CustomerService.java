package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.customer.CustomerBalanceDto;
import com.hardware.selvahardware.dto.customer.CustomerDto;
import com.hardware.selvahardware.dto.customer.CustomerRequest;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Invoice;
import com.hardware.selvahardware.enums.CustomerType;
import com.hardware.selvahardware.exception.BusinessException;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.mapper.CustomerMapper;
import com.hardware.selvahardware.repository.CustomerRepository;
import com.hardware.selvahardware.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final UUID WALK_IN_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final InvoiceRepository invoiceRepository;

    public List<CustomerDto> getAllCustomers(CustomerType type, String search) {
        return customerRepository.searchCustomers(type, search).stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CustomerDto> getCustomersByContractor(UUID contractorId) {
        return customerRepository.findByContractorId(contractorId).stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return customerMapper.toDto(customer);
    }

    public CustomerDto createCustomer(CustomerRequest request) {
        Customer customer = customerMapper.toEntity(request, null);
        return customerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDto updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customerMapper.updateEntity(customer, request);
        return customerMapper.toDto(customerRepository.save(customer));
    }

    public void deleteCustomer(UUID id) {
        if (WALK_IN_ID.equals(id)) {
            throw new BusinessException("Cannot delete walk-in customer");
        }
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", id);
        }
        customerRepository.deleteById(id);
    }

    public CustomerBalanceDto getCustomerBalance(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", customerId);
        }
        List<Invoice> invoices = invoiceRepository.findByCustomerIdOrderByCreatedAtAsc(customerId);

        BigDecimal totalBilled = invoices.stream()
                .map(Invoice::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = invoices.stream()
                .map(Invoice::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingBalance = invoices.stream()
                .map(Invoice::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CustomerBalanceDto.builder()
                .totalBilled(totalBilled)
                .totalPaid(totalPaid)
                .pendingBalance(pendingBalance)
                .invoiceCount(invoices.size())
                .build();
    }
}
