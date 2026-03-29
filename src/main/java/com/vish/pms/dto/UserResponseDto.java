package com.vish.pms.dto;

import java.util.UUID;


public record UserResponseDto(
        UUID id,
        String name,
        String email) {

}
