package com.hardware.selvahardware.mapper;

import com.hardware.selvahardware.dto.user.CreateUserRequest;
import com.hardware.selvahardware.dto.user.UserDto;
import com.hardware.selvahardware.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(CreateUserRequest req, String id, String encodedPassword) {
        return User.builder()
                .id(id)
                .name(req.getName())
                .email(req.getEmail())
                .passwordHash(encodedPassword)
                .phone(req.getPhone())
                .role(req.getRole())
                .isActive(true)
                .build();
    }
}
