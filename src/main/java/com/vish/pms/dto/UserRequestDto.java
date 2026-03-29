package com.vish.pms.dto;

import com.vish.pms.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "Name is required") @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters") String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid Email formt") String email,

        @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be atleast 6 characters long") String password
) {
}