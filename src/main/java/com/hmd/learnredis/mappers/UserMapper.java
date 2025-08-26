package com.hmd.learnredis.mappers;

import com.hmd.learnredis.dtos.UserDTO;
import com.hmd.learnredis.models.Role;
import com.hmd.learnredis.models.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()))
                .build();
    }
}
