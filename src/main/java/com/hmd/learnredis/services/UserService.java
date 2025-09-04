package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.UserDTO;
import com.hmd.learnredis.dtos.requests.CreateUserRequest;
import com.hmd.learnredis.dtos.requests.SearchRequest;
import com.hmd.learnredis.dtos.requests.UpdatePasswordRequest;
import com.hmd.learnredis.dtos.requests.UpdateUserRequest;
import com.hmd.learnredis.dtos.responses.Meta;
import com.hmd.learnredis.dtos.responses.PaginatedResponse;
import com.hmd.learnredis.exceptions.AlreadyExistsException;
import com.hmd.learnredis.exceptions.NotFoundException;
import com.hmd.learnredis.exceptions.PasswordException;
import com.hmd.learnredis.mappers.UserMapper;
import com.hmd.learnredis.models.Role;
import com.hmd.learnredis.models.User;
import com.hmd.learnredis.repositories.RoleRepository;
import com.hmd.learnredis.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    private final RedisService redisService;

    @Transactional
    public PaginatedResponse getUsers(SearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage() - 1, searchRequest.getPageSize());
        Page<UserDTO> results = userRepository.findAll(pageable).map(userMapper::toUserDTO);
        return PaginatedResponse.builder()
                .meta(Meta.builder()
                        .page(results.getNumber() + 1)
                        .size(results.getSize())
                        .totalPages(results.getTotalPages())
                        .totalElements(results.getTotalElements())
                        .build())
                .data(results.getContent())
                .build();
    }

    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new AlreadyExistsException(String.format("Username %s already exists", request.getUsername()));
        Set<Role> roles = request.getRoles().stream().map(roleName -> roleRepository.findByRoleName(roleName).orElseThrow(() -> new NotFoundException("Role not found: " + roleName))).collect(Collectors.toSet());
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();
        User savedUser = userRepository.save(newUser);
        UserDTO userDTO = userMapper.toUserDTO(savedUser);
        redisService.put(String.format("user:%s", savedUser.getId()), userDTO);
        return userDTO;
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
        if (user.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ADMIN")))
            throw new AccessDeniedException("You are not authorized to perform this operation");
        if (!user.getUsername().equals(request.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent())
                throw new AlreadyExistsException(String.format("Username %s already exists", request.getUsername()));
            user.setUsername(request.getUsername());
        }
        Set<Role> roles = request.getRoles().stream().map(roleName -> roleRepository.findByRoleName(roleName).orElseThrow(() -> new NotFoundException("Role not found: " + roleName))).collect(Collectors.toSet());
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        UserDTO userDTO = userMapper.toUserDTO(savedUser);
        redisService.put(String.format("user:%s", id), userDTO);
        return userDTO;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
        if (user.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ADMIN")))
            throw new AccessDeniedException("You are not authorized to perform this operation");
        userRepository.deleteById(id);
        redisService.delete(String.format("user:%s", id));
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        String key = String.format("user:%s", id);
        Optional<Object> cachedUser = redisService.get(key);

        if (cachedUser.isPresent())
            return (UserDTO) cachedUser.get();
        if (redisService.get(key, UserDTO.class).isPresent())
            return redisService.get(key, UserDTO.class).get();
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
        return userMapper.toUserDTO(user);
    }

    @Transactional
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new PasswordException("Provided old password does not match the current password");
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new PasswordException("New password is the same as old password");
        if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
            throw new PasswordException("Confirm password mismatch");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
