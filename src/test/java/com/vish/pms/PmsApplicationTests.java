package com.vish.pms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vish.pms.controller.AdminController;
import com.vish.pms.controller.ProductController;
import com.vish.pms.controller.UserController;
import com.vish.pms.dto.ProductRequestDto;
import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.entity.Cart;
import com.vish.pms.entity.Product;
import com.vish.pms.entity.User;
import com.vish.pms.service.serviceimpl.CartService;
import com.vish.pms.service.serviceimpl.ProductService;
import com.vish.pms.service.serviceimpl.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest({UserController.class, AdminController.class, ProductController.class})
@AutoConfiguration
class ControllerTestSuite {

}