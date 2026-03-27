package com.vish.pms.dto;

import java.util.UUID;

import com.vish.pms.enums.Role;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        Role role) {

}
