package com.hardware.selvahardware.dto.user;

import com.hardware.selvahardware.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    // Password is optional for updates - only update if provided
    private String password;

    @NotNull
    private UserRole role;

    private String phone;
    
    private boolean active;
}
