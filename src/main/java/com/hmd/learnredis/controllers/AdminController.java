package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.ResponseDTO;
import com.hmd.learnredis.dtos.requests.CreateUserRequest;
import com.hmd.learnredis.dtos.requests.SearchRequest;
import com.hmd.learnredis.dtos.requests.UpdateUserRequest;
import com.hmd.learnredis.dtos.responses.PaginatedResponse;
import com.hmd.learnredis.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PaginatedResponse getUsers(@ModelAttribute SearchRequest request) {
        return userService.getUsers(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO findUser(@PathVariable Long id) {
        return ResponseDTO.builder()
                .message("Successfully retrieved user")
                .data(userService.getUserById(id))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseDTO.builder()
                .message("Successfully created new user")
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        return ResponseDTO.builder()
                .message("Successfully updated user")
                .data(userService.updateUser(id, updateUserRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
