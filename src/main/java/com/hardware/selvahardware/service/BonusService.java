package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.bonus.BonusDto;
import com.hardware.selvahardware.dto.bonus.BonusRequest;
import com.hardware.selvahardware.entity.Bonus;
import com.hardware.selvahardware.entity.Customer;
import com.hardware.selvahardware.exception.ResourceNotFoundException;
import com.hardware.selvahardware.repository.BonusRepository;
import com.hardware.selvahardware.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BonusService {

    private final BonusRepository bonusRepository;
    private final CustomerRepository customerRepository;

    public BonusDto createBonus(BonusRequest request) {
        log.info("Creating bonus for customer: {}", request.getCustomerId());
        
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if (bonusRepository.existsByCustomer_IdAndBonusMonthAndBonusYear(
                request.getCustomerId(), request.getBonusMonth(), request.getBonusYear())) {
            throw new IllegalArgumentException("Bonus already exists for this customer in the specified month and year");
        }

        Bonus bonus = Bonus.builder()
                .customer(customer)
                .customerName(customer.getName())
                .bonusAmount(request.getBonusAmount())
                .bonusMonth(request.getBonusMonth())
                .bonusYear(request.getBonusYear())
                .reason(request.getReason())
                .build();

        Bonus savedBonus = bonusRepository.save(bonus);
        log.info("Bonus created successfully with id: {}", savedBonus.getId());
        
        return convertToDto(savedBonus);
    }

    @Transactional(readOnly = true)
    public List<BonusDto> getAllBonuses() {
        log.info("Fetching all bonuses");
        List<Bonus> bonuses = bonusRepository.findAll();
        return bonuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BonusDto> getBonusesByCustomerId(UUID customerId) {
        log.info("Fetching bonuses for customer: {}", customerId);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        List<Bonus> bonuses = bonusRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId);
        return bonuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BonusDto> getBonusesByCustomerAndYear(UUID customerId, Integer year) {
        log.info("Fetching bonuses for customer: {} for year: {}", customerId, year);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        List<Bonus> bonuses = bonusRepository.findByCustomerIdAndYear(customerId, year);
        return bonuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BonusDto> getBonusesByMonthAndYear(Integer month, Integer year) {
        log.info("Fetching bonuses for month: {} and year: {}", month, year);
        
        List<Bonus> bonuses = bonusRepository.findByMonthAndYear(month, year);
        return bonuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getTotalBonusForCustomerByYear(UUID customerId, Integer year) {
        log.info("Calculating total bonus for customer: {} for year: {}", customerId, year);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        return bonusRepository.getTotalBonusForCustomerByYear(customerId, year);
    }

    @Transactional(readOnly = true)
    public Double getTotalBonusForCustomerByMonthAndYear(UUID customerId, Integer year, Integer month) {
        log.info("Calculating total bonus for customer: {} for month: {} and year: {}", customerId, month, year);
        
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        
        return bonusRepository.getTotalBonusForCustomerByMonthAndYear(customerId, year, month);
    }

    public BonusDto updateBonus(UUID bonusId, BonusRequest request) {
        log.info("Updating bonus with id: {}", bonusId);
        
        Bonus bonus = bonusRepository.findById(bonusId)
                .orElseThrow(() -> new ResourceNotFoundException("Bonus not found with id: " + bonusId));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if (!bonus.getCustomer().getId().equals(request.getCustomerId()) &&
            bonusRepository.existsByCustomer_IdAndBonusMonthAndBonusYear(
                    request.getCustomerId(), request.getBonusMonth(), request.getBonusYear())) {
            throw new IllegalArgumentException("Bonus already exists for this customer in the specified month and year");
        }

        bonus.setCustomer(customer);
        bonus.setCustomerName(customer.getName());
        bonus.setBonusAmount(request.getBonusAmount());
        bonus.setBonusMonth(request.getBonusMonth());
        bonus.setBonusYear(request.getBonusYear());
        bonus.setReason(request.getReason());

        Bonus updatedBonus = bonusRepository.save(bonus);
        log.info("Bonus updated successfully with id: {}", updatedBonus.getId());
        
        return convertToDto(updatedBonus);
    }

    public void deleteBonus(UUID bonusId) {
        log.info("Deleting bonus with id: {}", bonusId);
        
        Bonus bonus = bonusRepository.findById(bonusId)
                .orElseThrow(() -> new ResourceNotFoundException("Bonus not found with id: " + bonusId));
        
        bonusRepository.delete(bonus);
        log.info("Bonus deleted successfully with id: {}", bonusId);
    }

    private BonusDto convertToDto(Bonus bonus) {
        return BonusDto.builder()
                .id(bonus.getId())
                .customerId(bonus.getCustomer().getId())
                .customerName(bonus.getCustomerName())
                .bonusAmount(bonus.getBonusAmount())
                .bonusMonth(bonus.getBonusMonth())
                .bonusYear(bonus.getBonusYear())
                .reason(bonus.getReason())
                .createdAt(bonus.getCreatedAt())
                .updatedAt(bonus.getUpdatedAt())
                .build();
    }
}
