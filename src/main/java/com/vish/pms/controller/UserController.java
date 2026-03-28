package com.vish.pms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vish.pms.config.CustomUserDetails;
import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.dto.UserResponseDto;
import com.vish.pms.entity.User;
import com.vish.pms.mapping.UserMapper;
import com.vish.pms.service.serviceimpl.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new")
    public UserResponseDto createNewUser (@RequestBody @Valid UserRequestDto userRequestDto) {
        //TODO: process POST request

        User user = UserMapper.toEntity(userRequestDto);
        User savedUser  =  userService.create(user);
        return UserMapper.toResponse(savedUser); 
    }

    @GetMapping("/me")
    public UserResponseDto profile(@AuthenticationPrincipal CustomUserDetails userDetails)  {
        User user = userService.getByID(userDetails.getId());
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    @PutMapping("/me")
    public UserResponseDto updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserRequestDto userRequestDto) {
        //TODO: process PUT request
        
        User newUser = User.builder()
                            .name(userRequestDto.name())
                            .password(userRequestDto.password())
                            .email(userRequestDto.email())
                            .role(userRequestDto.role())
                            .build();
        
        User updatedUser = userService.update(userDetails.getId(), newUser);
        return new UserResponseDto(updatedUser.getId(),updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRole());
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        //TODO: process PUT request

        userService.deleteById(userDetails.getId());
        return ResponseEntity.ok("User deleted successfully");
    }
    

}
