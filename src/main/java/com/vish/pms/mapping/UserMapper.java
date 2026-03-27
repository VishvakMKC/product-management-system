package com.vish.pms.mapping;

import com.vish.pms.dto.UserRequestDto;
import com.vish.pms.dto.UserResponseDto;
import com.vish.pms.entity.User;

public class UserMapper {
    public static User toEntity(UserRequestDto userRequestDto) {
        if (userRequestDto == null)
            return null;
        return User.builder().email(userRequestDto.email())
                .name(userRequestDto.name())
                .password(userRequestDto.password())
                .role(userRequestDto.role())
                .build();
    }

    public static UserResponseDto toResponse(User user) {
        if (user == null)
            return null;
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
