package com.vish.pms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.dto.UserResponseDto;
import com.vish.pms.entity.User;
import com.vish.pms.mapping.UserMapper;
import com.vish.pms.service.serviceimpl.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public UserResponseDto createNewUser (@RequestBody @Valid UserRequestDto userRequestDto) {
        //TODO: process POST request
        User user = UserMapper.toEntity(userRequestDto);
        User savedUser  =  userService.create(user);
        return UserMapper.toResponse(savedUser); 
    }

    @GetMapping("/{id}/profile")
    public UserResponseDto profile(@PathVariable(name = "id" ) UUID id)  {
        User user = userService.getByID(id);
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    @PutMapping("/{id}/update")
    public UserResponseDto updateUser(@PathVariable(name = "id") UUID id, @Valid @RequestBody UserRequestDto userRequestDto) {
        //TODO: process PUT request

        User newUser = User.builder()
                            .name(userRequestDto.name())
                            .password(userRequestDto.password())
                            .email(userRequestDto.email())
                            .role(userRequestDto.role())
                            .build();
        
        User updatedUser = userService.update(id, newUser);
        return new UserResponseDto(updatedUser.getId(),updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRole());
    }
    
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") UUID id) {
        //TODO: process PUT request

        userService.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
    

    @GetMapping("/getAll")
    public List<UserResponseDto> getAllUsers(
        @RequestParam(defaultValue = "0" ) int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "name") String sortBy,
        @RequestParam(defaultValue = "asc")  String dir
    ) {
        return userService.getAll(page, size, sortBy, dir).stream()
                    .map(UserMapper::toResponse)
                    .toList();
    }
    
}
