package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.ResponseDTO;
import com.hmd.learnredis.dtos.requests.LoginRequest;
import com.hmd.learnredis.dtos.requests.RefreshTokenRequest;
import com.hmd.learnredis.dtos.requests.RegisterRequest;
import com.hmd.learnredis.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseDTO.builder()
                .message("Successfully logged in")
                .data(authService.login(loginRequest))
                .build();
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return ResponseDTO.builder()
                .message("Successfully refreshed token")
                .data(authService.refreshToken(refreshTokenRequest))
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseDTO.builder()
                .message("Successfully registered account")
                .data(authService.register(registerRequest))
                .build();
    }
}
