package com.vish.pms.service.serviceimpl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vish.pms.entity.Cart;
import com.vish.pms.entity.CartItem;
import com.vish.pms.entity.Product;
import com.vish.pms.entity.User;
import com.vish.pms.repository.CartItemRepository;
import com.vish.pms.repository.CartRepository;
import com.vish.pms.repository.ProductRepository;
import com.vish.pms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 🔹 Get Cart
    public Cart getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    // 🔹 Add Item
    public Cart addItemToCart(UUID userId, UUID productId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Cart cart = getCartByUserId(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 🔥 STOCK VALIDATION (IMPORTANT)
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock available");
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (item != null) {
            int newQuantity = item.getQuantity() + quantity;

            if (product.getQuantity() < newQuantity) {
                throw new RuntimeException("Exceeds available stock");
            }

            item.setQuantity(newQuantity);
            item.setPrice(product.getPrice());

        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());

            cart.getItems().add(item);
        }

        updateTotal(cart);

        return cartRepository.save(cart);
    }

    // 🔹 Remove Item
    public Cart removeItemFromCart(UUID userId, UUID productId) {

        Cart cart = getCartByUserId(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.getItems().remove(item); // orphanRemoval will delete

        updateTotal(cart);

        return cartRepository.save(cart);
    }

    // 🔹 Create Cart
    private Cart createCart(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);

        return cartRepository.save(cart);
    }

    // 🔹 Update Total Price
    private void updateTotal(Cart cart) {

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
    }
}