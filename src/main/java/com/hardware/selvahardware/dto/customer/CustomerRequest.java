package com.hardware.selvahardware.dto.customer;

import com.hardware.selvahardware.enums.CustomerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CustomerRequest {
    @NotBlank
    private String name;

    private String phone;
    private String email;
    private String address;
    private String city;

    @NotNull
    private CustomerType type;

    private Boolean isActive;

    private UUID contractorId;
}
