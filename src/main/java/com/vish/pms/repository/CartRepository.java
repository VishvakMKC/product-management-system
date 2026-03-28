package com.vish.pms.repository;

import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vish.pms.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(UUID userId);
}
