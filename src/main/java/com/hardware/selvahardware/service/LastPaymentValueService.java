package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.lastpaymentvalue.LastPaymentValueDto;
import com.hardware.selvahardware.dto.lastpaymentvalue.LastPaymentValueRequest;
import com.hardware.selvahardware.entity.LastPaymentValue;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.entity.Product;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.repository.LastPaymentValueRepository;
import com.hardware.selvahardware.repository.CustomerRepository;
import com.hardware.selvahardware.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LastPaymentValueService {

    private final LastPaymentValueRepository lastPaymentValueRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public LastPaymentValueDto createOrUpdateLastPaymentValue(LastPaymentValueRequest request) {
        log.info("Creating or updating last payment value for customer: {} and product: {}", 
                request.getCustomerId(), request.getProductId());
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        LastPaymentValue lastPaymentValue = lastPaymentValueRepository
                .findByCustomer_IdAndProduct_Id(request.getCustomerId(), request.getProductId())
                .orElse(LastPaymentValue.builder()
                        .customer(customer)
                        .product(product)
                        .build());

        lastPaymentValue.setCustomer(customer);
        lastPaymentValue.setProduct(product);
        lastPaymentValue.setProductName(product.getName());
        lastPaymentValue.setLastAmount(request.getLastAmount());
        lastPaymentValue.setLastQuantity(request.getLastQuantity());
        lastPaymentValue.setLastUnitPrice(request.getLastUnitPrice());
        lastPaymentValue.setLastDiscount(request.getLastDiscount() != null ? request.getLastDiscount() : BigDecimal.ZERO);
        lastPaymentValue.setLastGstRate(request.getLastGstRate() != null ? request.getLastGstRate() : BigDecimal.ZERO);
        lastPaymentValue.setBillDate(request.getBillDate());
        lastPaymentValue.setInvoiceNumber(request.getInvoiceNumber());

        LastPaymentValue savedValue = lastPaymentValueRepository.save(lastPaymentValue);
        log.info("Last payment value saved successfully with id: {}", savedValue.getId());
        
        return convertToDto(savedValue);
    }

    @Transactional(readOnly = true)
    public List<LastPaymentValueDto> getLastPaymentValuesByCustomerId(UUID customerId) {
        log.info("Fetching last payment values for customer: {}", customerId);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        List<LastPaymentValue> values = lastPaymentValueRepository.findByCustomer_IdOrderByUpdatedAtDesc(customerId);
        return values.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LastPaymentValueDto getLastPaymentValueByCustomerAndProduct(UUID customerId, UUID productId) {
        log.info("Fetching last payment value for customer: {} and product: {}", customerId, productId);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        
        LastPaymentValue value = lastPaymentValueRepository.findByCustomer_IdAndProduct_Id(customerId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Last payment value not found for customer: " + customerId + " and product: " + productId));
        
        return convertToDto(value);
    }

    @Transactional(readOnly = true)
    public List<LastPaymentValueDto> getLastPaymentValuesByProductId(UUID productId) {
        log.info("Fetching last payment values for product: {}", productId);
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        
        List<LastPaymentValue> values = lastPaymentValueRepository.findLastPaymentValuesByProduct(productId);
        return values.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LastPaymentValueDto> searchByProductName(UUID customerId, String productName) {
        log.info("Searching last payment values for customer: {} with product name containing: {}", customerId, productName);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        List<LastPaymentValue> values = lastPaymentValueRepository.findByCustomerIdAndProductNameContaining(customerId, productName);
        return values.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteLastPaymentValue(UUID customerId, UUID productId) {
        log.info("Deleting last payment value for customer: {} and product: {}", customerId, productId);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        
        if (!lastPaymentValueRepository.findByCustomer_IdAndProduct_Id(customerId, productId).isPresent()) {
            throw new ResourceNotFoundException(
                    "Last payment value not found for customer: " + customerId + " and product: " + productId);
        }
        
        lastPaymentValueRepository.deleteByCustomer_IdAndProduct_Id(customerId, productId);
        log.info("Last payment value deleted successfully for customer: {} and product: {}", customerId, productId);
    }

    private LastPaymentValueDto convertToDto(LastPaymentValue value) {
        return LastPaymentValueDto.builder()
                .id(value.getId())
                .customerId(value.getCustomer().getId())
                .productId(value.getProduct().getId())
                .productName(value.getProductName())
                .lastAmount(value.getLastAmount())
                .lastQuantity(value.getLastQuantity())
                .lastUnitPrice(value.getLastUnitPrice())
                .lastDiscount(value.getLastDiscount())
                .lastGstRate(value.getLastGstRate())
                .billDate(value.getBillDate())
                .invoiceNumber(value.getInvoiceNumber())
                .createdAt(value.getCreatedAt())
                .updatedAt(value.getUpdatedAt())
                .build();
    }
}
