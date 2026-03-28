package com.vish.pms.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vish.pms.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    List<CartItem> findByCartId(UUID cartId);

    void deleteByCartIdAndProductId(UUID cartId, UUID productId);
}
