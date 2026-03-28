package com.hardware.selvahardware.service;

import com.hardware.selvahardware.dto.auth.LoginRequest;
import com.hardware.selvahardware.dto.auth.LoginResponse;
import com.hardware.selvahardware.entity.User;
import com.hardware.selvahardware.enums.UserRole;
import com.hardware.selvahardware.repository.UserRepository;
import com.hardware.selvahardware.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);
        List<String> permissions = getPermissions(user.getRole());

        return LoginResponse.builder()
                .success(true)
                .user(LoginResponse.UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .token(token)
                .permissions(permissions)
                .build();
    }

    private List<String> getPermissions(UserRole role) {
        return switch (role) {
            case admin -> List.of("dashboard", "billing", "products", "customers", "invoices", "reports", "admin", "settings");
            case staff -> List.of("dashboard", "billing", "products", "customers", "invoices");
            case owner -> List.of("dashboard", "billing", "products", "customers", "invoices", "reports", "admin", "settings");
            case manager -> List.of("dashboard", "billing", "products", "customers", "invoices", "reports");
            case accountant -> List.of("dashboard", "billing", "products", "customers", "invoices", "reports");
            case employee -> List.of("dashboard", "billing", "products", "customers", "invoices");
            case superadmin -> List.of("dashboard", "billing", "products", "customers", "invoices", "reports", "admin", "settings");
        };
    }
}
