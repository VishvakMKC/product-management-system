package com.vish.pms.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponseDto(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal price) {

}
