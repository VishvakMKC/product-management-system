package com.vish.pms.mapping;

import java.util.List;

import com.vish.pms.dto.CartItemResponseDto;
import com.vish.pms.dto.CartResponseDto;
import com.vish.pms.entity.Cart;

public class CartMapper {

    public static CartResponseDto mapToCartResponse(Cart cart) {
        List<CartItemResponseDto> items = cart.getItems().stream()
                .map(item -> new CartItemResponseDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()))
                .toList();

        return new CartResponseDto(
                cart.getId(),
                items,
                cart.getTotalPrice());
    }
}