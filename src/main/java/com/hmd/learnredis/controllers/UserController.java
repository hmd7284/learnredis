package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.ResponseDTO;
import com.hmd.learnredis.dtos.requests.UpdatePasswordRequest;
import com.hmd.learnredis.models.CustomUserDetails;
import com.hmd.learnredis.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getMe(@AuthenticationPrincipal CustomUserDetails me) {
        return ResponseDTO.builder()
                .message("Successfully retrieved personal info")
                .data(userService.getUserById(me.getId()))
                .build();
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@AuthenticationPrincipal CustomUserDetails me, UpdatePasswordRequest request) {
        userService.updatePassword(me.getId(), request);
    }
}
