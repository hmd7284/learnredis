package com.hmd.learnredis.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateUserRequest {
    private String username;
    private String password;
    private Set<String> roles;
}
