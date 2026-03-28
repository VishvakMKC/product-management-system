package com.vish.pms.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vish.pms.dto.ProductRequestDto;
import com.vish.pms.dto.ProductResponseDto;
import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.dto.UserResponseDto;
import com.vish.pms.entity.Product;
import com.vish.pms.entity.User;
import com.vish.pms.mapping.ProductMapper;
import com.vish.pms.mapping.UserMapper;
import com.vish.pms.service.serviceimpl.ProductService;
import com.vish.pms.service.serviceimpl.UserService;

import jakarta.validation.Valid;

@RequestMapping("/admin")
@RestController
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    public AdminController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    // ===================== USERS =====================
    @GetMapping("/users/search")
    public List<UserResponseDto> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String dir) {

        int adjustedPage = Math.max(page - 1, 0);

        return userService.searchUsers(email, name, role, adjustedPage, size, sortBy, dir)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String dir) {

        return userService.getAll(page, size, sortBy, dir)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @PostMapping("/users/bulk")
    public List<UserResponseDto> createAllUsers(@RequestBody @Valid List<UserRequestDto> userRequestDtos) {
        //TODO: process POST request
        List<User> users = userRequestDtos.stream().map(UserMapper::toEntity).toList();
        List<User> savedUsers = userService.createAll(users); 
        return savedUsers.stream().map(UserMapper::toResponse).toList();
    }
    

    @GetMapping("/users/{id}")
    public UserResponseDto getUserById(@PathVariable UUID id) {
        return UserMapper.toResponse(userService.getByID(id));
    }

    @PostMapping("/users")
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toResponse(userService.create(user));
    }

    @PutMapping("/users/{id}")
    public UserResponseDto updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDto dto) {

        User user = UserMapper.toEntity(dto);
        return UserMapper.toResponse(userService.update(id, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // ===================== PRODUCTS =====================

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

    @GetMapping("/products/{id}")
    public ProductResponseDto getProductById(@PathVariable UUID id) {
        return ProductMapper.toResponse(productService.getByID(id));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto dto) {

        Product product = ProductMapper.toEntity(dto);
        Product saved = productService.create(product);

        return ResponseEntity.status(201).body(ProductMapper.toResponse(saved));
    }

    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDto dto) {

        Product product = ProductMapper.toEntity(dto);
        return ProductMapper.toResponse(productService.update(id, product));
    }

    @PostMapping("/products/bulk")
    public ResponseEntity<List<ProductResponseDto>> createProducts(
            @Valid @RequestBody List<ProductRequestDto> requestDtos) {

        List<Product> products = requestDtos.stream()
                .map(ProductMapper::toEntity)
                .toList();

        List<Product> savedProducts = productService.createAll(products);

        List<ProductResponseDto> response = savedProducts.stream()
                .map(ProductMapper::toResponse)
                .toList();

        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
