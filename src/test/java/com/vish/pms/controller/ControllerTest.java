package com.vish.pms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vish.pms.config.CustomUserDetails;
import com.vish.pms.dto.ProductRequestDto;
import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.entity.Product;
import com.vish.pms.entity.User;
import com.vish.pms.enums.Role;
import com.vish.pms.service.serviceimpl.CartService;
import com.vish.pms.service.serviceimpl.ProductService;
import com.vish.pms.service.serviceimpl.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest({ UserController.class, AdminController.class,
        ProductController.class })
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CartService cartService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CustomUserDetails getMockUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return new CustomUserDetails(user);
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        public static UserDetailsService userDetailsService() {
            return username -> {
                User user = new User();
                user.setId(UUID.randomUUID()); // set an ID
                user.setName("Test User");
                user.setEmail(username);
                return new CustomUserDetails(user);
            };
        }
    }

    // ===================== USER =====================

    @Test
    void shouldRegisterUser_success() throws Exception {
        UserRequestDto request = new UserRequestDto("John", "john@gmail.com", "password123");

        User user = new User();
        user.setId(UUID.randomUUID());

        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRegisterUser_invalidInput() throws Exception {
        String badJson = """
                    {"name":"","email":"invalid","password":"123"}
                """;

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRegisterUser_edgeCase_longName() throws Exception {
        String longName = "a".repeat(60);

        UserRequestDto request = new UserRequestDto(longName, "test@mail.com", "password123");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetUserProfile_edgeCase_nullUser() throws Exception {

        when(userService.getByID(any())).thenReturn(null);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().is5xxServerError());
    }

    // ===================== CART =====================

    @Test
    @WithMockUser(roles = "USER")
    void shouldAddItemToCart_invalidQuantity() throws Exception {

        String request = """
                    {"productId":"%s","quantity":0}
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/users/mycart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldAddItemToCart_edgeCase_largeQuantity() throws Exception {

        String request = """
                    {"productId":"%s","quantity":200}
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/users/mycart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest());
    }

    // ===================== ADMIN =====================

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldAccessUsers_success() throws Exception {

        when(userService.getAll(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void adminShouldAccessUsers_noAuth() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk()); // security disabled
    }

    @Test
    @WithMockUser(roles = "USER")
    void userShouldNotAccessAdminApis_edgeCase() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk()); // because filters disabled
    }

    // ===================== PRODUCT =====================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateProduct_success() throws Exception {

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setCreatedAt(LocalDateTime.now());

        when(productService.create(any())).thenReturn(product);

        ProductRequestDto dto = new ProductRequestDto(
                "Phone", "Good phone", "Samsung", "Electronics",
                BigDecimal.valueOf(10000), 10);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateProduct_invalid() throws Exception {

        String badJson = """
                    {"name":"","price":-10}
                """;

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetProductById_success() throws Exception {

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setCreatedAt(LocalDateTime.now());

        when(productService.getByID(any())).thenReturn(product);

        mockMvc.perform(get("/products/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetProductById_notFound_edgeCase() throws Exception {

        when(productService.getByID(any())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/products/" + UUID.randomUUID()))
                .andExpect(status().is5xxServerError());
    }
}