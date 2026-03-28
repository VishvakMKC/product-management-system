package com.vish.pms.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequestDto(

        @NotNull(message = "Product ID cannot be null") UUID productId,

        @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 100, message = "Quantity cannot exceed 100") Integer quantity

) {
}