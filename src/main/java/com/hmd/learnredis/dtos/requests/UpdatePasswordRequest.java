package com.hmd.learnredis.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdatePasswordRequest {
    @NotBlank(message = "Old password is required")
    private String oldPassword;
    @NotBlank(message = "New password is required")
    private String newPassword;
    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;
}
