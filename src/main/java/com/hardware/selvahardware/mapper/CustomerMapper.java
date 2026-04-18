package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.customer.CustomerDto;
import com.hardware.selvahardware.dto.customer.CustomerRequest;
import com.hardware.selvahardware.entity.Customer;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .city(customer.getCity())
                .type(customer.getType())
                .isActive(customer.getIsActive())
                .lastPaymentDate(customer.getLastPaymentDate())
                .lastInvoiceDate(customer.getLastInvoiceDate())
                .contractorId(customer.getContractorId())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public Customer toEntity(CustomerRequest req, UUID id) {
        return Customer.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .city(req.getCity())
                .type(req.getType())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .contractorId(req.getContractorId())
                .build();
    }

    public void updateEntity(Customer customer, CustomerRequest req) {
        customer.setName(req.getName());
        customer.setPhone(req.getPhone());
        customer.setEmail(req.getEmail());
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setType(req.getType());
        if (req.getIsActive() != null) {
            customer.setIsActive(req.getIsActive());
        }
        customer.setContractorId(req.getContractorId());
    }
}
