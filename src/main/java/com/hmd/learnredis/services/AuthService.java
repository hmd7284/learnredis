package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.UserDTO;
import com.hmd.learnredis.dtos.requests.LoginRequest;
import com.hmd.learnredis.dtos.requests.RefreshTokenRequest;
import com.hmd.learnredis.dtos.requests.RegisterRequest;
import com.hmd.learnredis.dtos.responses.LoginResponse;
import com.hmd.learnredis.dtos.responses.RefreshTokenResponse;
import com.hmd.learnredis.exceptions.NotFoundException;
import com.hmd.learnredis.exceptions.PasswordException;
import com.hmd.learnredis.exceptions.AlreadyExistsException;
import com.hmd.learnredis.mappers.UserMapper;
import com.hmd.learnredis.models.RefreshToken;
import com.hmd.learnredis.models.Role;
import com.hmd.learnredis.models.User;
import com.hmd.learnredis.repositories.RoleRepository;
import com.hmd.learnredis.repositories.UserRepository;
import com.hmd.learnredis.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String roleName = request.getRoleName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username or password is incorrect"));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("Username or password is incorrect");
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
        if (!user.getRoles().contains(role))
            throw new AccessDeniedException("Access denied! Missing required role: " + roleName);
        return LoginResponse.builder()
                .accessToken(jwtUtils.generateToken(username))
                .refreshToken(refreshTokenService.createRefreshToken(user))
                .build();
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);
        if (refreshTokenService.verifyExpiration(refreshToken)) {
            User user = refreshToken.getUser();
            String newAccessToken = jwtUtils.generateToken(user.getUsername());
            String newRefreshToken = refreshTokenService.createRefreshToken(user);
            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        }
        return null;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = jwtUtils.extractToken(request);
        if (jwtUtils.validateToken(accessToken)) {
            String username = jwtUtils.extractUsername(accessToken);
            userRepository.findByUsername(username).ifPresent(refreshTokenService::deleteByUser);
        }
    }

    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new AlreadyExistsException(String.format("Username %s already exists", request.getUsername()));
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new PasswordException("Confirm password mismatch");
        if (request.getRoles().contains("ADMIN"))
            throw new AccessDeniedException("Can't register admin account");
        Set<Role> roles = request.getRoles().stream().map(roleName -> roleRepository.findByRoleName(roleName).orElseThrow(() -> new NotFoundException("Role not found: " + roleName))).collect(Collectors.toSet());
        User savedUser = userRepository.save(User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build());
        return userMapper.toUserDTO(savedUser);
    }
}
