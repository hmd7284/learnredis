package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.UserDTO;
import com.hmd.learnredis.dtos.requests.CreateUserRequest;
import com.hmd.learnredis.dtos.requests.UpdateUserRequest;
import com.hmd.learnredis.exceptions.RoleNotFoundException;
import com.hmd.learnredis.exceptions.UserNotFoundException;
import com.hmd.learnredis.exceptions.UsernameAlreadyExistsException;
import com.hmd.learnredis.mappers.UserMapper;
import com.hmd.learnredis.models.Role;
import com.hmd.learnredis.models.User;
import com.hmd.learnredis.repositories.RoleRepository;
import com.hmd.learnredis.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    @CachePut(cacheNames = "users", key = "#result.id")
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new UsernameAlreadyExistsException(String.format("Username %s already exists", request.getUsername()));
        Set<Role> roles = request.getRoles().stream().map(roleName -> roleRepository.findByRoleName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName))).collect(Collectors.toSet());
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();
        User savedUser = userRepository.save(newUser);
        return userMapper.toUserDTO(savedUser);
    }

    @Transactional
    @CachePut(cacheNames = "users", key = "#id")
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", id)));
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId()))
            throw new UsernameAlreadyExistsException(String.format("Username %s already exists", request.getUsername()));
        user.setUsername(request.getUsername());
        Set<Role> roles = request.getRoles().stream().map(roleName -> roleRepository.findByRoleName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName))).collect(Collectors.toSet());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    @Transactional
    @CacheEvict(cacheNames = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", id)));
        return userMapper.toUserDTO(user);
    }

}
