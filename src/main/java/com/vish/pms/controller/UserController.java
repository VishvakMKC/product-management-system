package com.vish.pms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vish.pms.config.CustomUserDetails;
import com.vish.pms.dto.CartItemRequestDto;
import com.vish.pms.dto.CartResponseDto;
import com.vish.pms.dto.ProductResponseDto;
import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.dto.UserResponseDto;
import com.vish.pms.entity.Cart;
import com.vish.pms.entity.User;
import com.vish.pms.enums.Role;
import com.vish.pms.mapping.CartMapper;
import com.vish.pms.mapping.ProductMapper;
import com.vish.pms.mapping.UserMapper;
import com.vish.pms.service.serviceimpl.CartService;
import com.vish.pms.service.serviceimpl.ProductService;
import com.vish.pms.service.serviceimpl.UserService;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")

public class UserController {

    private UserService userService;
    private CartService cartService;
    private ProductService productService;
    public UserController(UserService userService, CartService cartService, ProductService productService) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/register")
    public UserResponseDto createNewUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        // TODO: process POST request

        User user = UserMapper.toEntity(userRequestDto);
        user.setRole(Role.USER);
        User savedUser = userService.create(user);
        return UserMapper.toResponse(savedUser);
    }

    @GetMapping("/me")
    public UserResponseDto profile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("UserDetails received: " + userDetails); // ✅ Debug print

        if (userDetails == null) {
            System.out.println("userDetails is null!");
            throw new RuntimeException("AuthenticationPrincipal is null");
        }

        User user = userService.getByID(userDetails.getId());
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    @PutMapping("/me")
    public UserResponseDto updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        // TODO: process PUT request

        User newUser = User.builder()
                .name(userRequestDto.name())
                .password(userRequestDto.password())
                .email(userRequestDto.email())
                .build();

        User updatedUser = userService.update(userDetails.getId(), newUser);
        return new UserResponseDto(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail());
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // TODO: process PUT request

        userService.deleteById(userDetails.getId());
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/mycart")
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(CartMapper.mapToCartResponse(cartService.getCartByUserId(customUserDetails.getId())));
    }

    @PostMapping("/mycart/items")
    public ResponseEntity<CartResponseDto> addItemToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartItemRequestDto request) {

        Cart cart = cartService.addItemToCart(
                userDetails.getId(),
                request.productId(),
                request.quantity());

        return ResponseEntity.ok(CartMapper.mapToCartResponse(cart));
    }
    @GetMapping("/products/search")
    public List<ProductResponseDto> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String dir) {

        int adjustedPage = Math.max(page - 1, 0);

        return productService.searchProducts(
                name, minPrice, maxPrice,
                adjustedPage, size, sortBy, dir)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    } 

    @GetMapping("/products")
    public List<ProductResponseDto> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String dir) {

        return productService.getAll(page > 0 ? page - 1 : page, size, sortBy, dir)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/mycart/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItemFromCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID productId) {

        Cart cart = cartService.removeItemFromCart(userDetails.getId(), productId);
        return ResponseEntity.ok(CartMapper.mapToCartResponse(cart));
    }

}
