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

    private static final String WALK_IN_ID = "walk-in";

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final InvoiceRepository invoiceRepository;

    public List<CustomerDto> getAllCustomers(CustomerType type, String search) {
        return customerRepository.searchCustomers(type, search).stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        return customerMapper.toDto(customer);
    }

    public CustomerDto createCustomer(CustomerRequest request) {
        String id = UUID.randomUUID().toString();
        Customer customer = customerMapper.toEntity(request, id);
        return customerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDto updateCustomer(String id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        customerMapper.updateEntity(customer, request);
        return customerMapper.toDto(customerRepository.save(customer));
    }

    public void deleteCustomer(String id) {
        if (WALK_IN_ID.equals(id)) {
            throw new BusinessException("Cannot delete walk-in customer");
        }
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", id);
        }
        customerRepository.deleteById(id);
    }

    public CustomerBalanceDto getCustomerBalance(String customerId) {
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
