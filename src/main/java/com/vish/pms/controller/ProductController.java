package com.vish.pms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vish.pms.dto.ProductRequestDto;
import com.vish.pms.dto.ProductResponseDto;
import com.vish.pms.entity.Product;
import com.vish.pms.mapping.ProductMapper;
import com.vish.pms.service.serviceimpl.ProductService;

import jakarta.validation.Valid;

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


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ Get all products

    // ✅ Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable UUID id) {
        Product product = productService.getByID(id);
        return ResponseEntity.ok(ProductMapper.toResponse(product));
    }

    // ✅ Create product
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {

        Product product = ProductMapper.toEntity(requestDto);
        Product saved = productService.create(product);

        return ResponseEntity
                .status(201)
                .body(ProductMapper.toResponse(saved));
    }

    @PostMapping("/bulk")
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

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        List<Product> productPage = productService.searchProducts(
                name, minPrice, maxPrice, page > 0 ? page = page - 1 : page, size, sortBy, direction);

        List<ProductResponseDto> response = productPage.stream().map(ProductMapper::toResponse).toList();

        return ResponseEntity.ok(response);
    }

    // ✅ Update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDto requestDto) {

        Product product = ProductMapper.toEntity(requestDto);
        Product updated = productService.update(id, product);

        return ResponseEntity.ok(ProductMapper.toResponse(updated));
    }

    // ✅ Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
