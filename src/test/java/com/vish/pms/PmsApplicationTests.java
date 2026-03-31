package com.vish.pms;

import org.springframework.boot.autoconfigure.AutoConfiguration;

import com.vish.pms.controller.AdminController;
import com.vish.pms.controller.ProductController;
import com.vish.pms.controller.UserController;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest({UserController.class, AdminController.class, ProductController.class})
@AutoConfiguration
class ControllerTestSuite {

}