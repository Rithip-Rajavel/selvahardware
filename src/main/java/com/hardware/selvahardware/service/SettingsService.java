package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.settings.SettingsDto;
import com.hardware.selvahardware.entity.ShopSettings;
import com.hardware.selvahardware.repository.ShopSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final ShopSettingsRepository settingsRepository;

    public SettingsDto getSettings() {
        List<ShopSettings> allSettings = settingsRepository.findAll();
        ShopSettings settings = allSettings.isEmpty() 
                ? ShopSettings.builder().name("").address("").build()
                : allSettings.get(0);
        return toDto(settings);
    }

    public SettingsDto updateSettings(SettingsDto dto) {
        List<ShopSettings> allSettings = settingsRepository.findAll();
        ShopSettings settings;
        
        if (allSettings.isEmpty()) {
            // Create new settings with generated ID
            settings = ShopSettings.builder()
                    .name(dto.getName())
                    .address(dto.getAddress())
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .gstNumber(dto.getGstNumber())
                    .bankName(dto.getBankName())
                    .accountNumber(dto.getAccountNumber())
                    .ifscCode(dto.getIfscCode())
                    .upiId(dto.getUpiId())
                    .defaultGstPercentage(dto.getDefaultGstPercentage())
                    .invoicePrefix(dto.getInvoicePrefix())
                    .termsAndConditions(dto.getTermsAndConditions())
                    .build();
        } else {
            // Update existing settings
            settings = allSettings.get(0);
            settings.setName(dto.getName());
            settings.setAddress(dto.getAddress());
            settings.setPhone(dto.getPhone());
            settings.setEmail(dto.getEmail());
            settings.setGstNumber(dto.getGstNumber());
            settings.setBankName(dto.getBankName());
            settings.setAccountNumber(dto.getAccountNumber());
            settings.setIfscCode(dto.getIfscCode());
            settings.setUpiId(dto.getUpiId());
            settings.setDefaultGstPercentage(dto.getDefaultGstPercentage());
            settings.setInvoicePrefix(dto.getInvoicePrefix());
            settings.setTermsAndConditions(dto.getTermsAndConditions());
        }
        
        return toDto(settingsRepository.save(settings));
    }

    private SettingsDto toDto(ShopSettings s) {
        return SettingsDto.builder()
                .name(s.getName())
                .address(s.getAddress())
                .phone(s.getPhone())
                .email(s.getEmail())
                .gstNumber(s.getGstNumber())
                .bankName(s.getBankName())
                .accountNumber(s.getAccountNumber())
                .ifscCode(s.getIfscCode())
                .upiId(s.getUpiId())
                .defaultGstPercentage(s.getDefaultGstPercentage())
                .invoicePrefix(s.getInvoicePrefix())
                .termsAndConditions(s.getTermsAndConditions())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
