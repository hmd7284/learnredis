package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.CreateUserRequest;
import com.hmd.learnredis.dtos.ResponseDTO;
import com.hmd.learnredis.dtos.UpdateUserRequest;
import com.hmd.learnredis.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> findUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Successfully retrieved user")
                .data(userService.getUserById(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.builder()
                .message("Successfully created new user")
                .data(userService.createUser(request))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Successfully updated user")
                .data(userService.updateUser(id, updateUserRequest))
                .build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
