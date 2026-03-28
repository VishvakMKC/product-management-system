package com.vish.pms.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponseDto(
        UUID cartId,
        List<CartItemResponseDto> items,
        BigDecimal totalPrice

) {
}