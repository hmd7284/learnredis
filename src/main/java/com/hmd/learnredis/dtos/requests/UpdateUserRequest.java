package com.hmd.learnredis.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotNull(message = "Roles are required")
    private Set<String> roles;
}
