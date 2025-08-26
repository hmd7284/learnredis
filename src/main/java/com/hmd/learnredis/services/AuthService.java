package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.requests.LoginRequest;
import com.hmd.learnredis.dtos.requests.RefreshTokenRequest;
import com.hmd.learnredis.dtos.responses.LoginResponse;
import com.hmd.learnredis.dtos.responses.RefreshTokenResponse;
import com.hmd.learnredis.exceptions.RoleNotFoundException;
import com.hmd.learnredis.models.CustomUserDetails;
import com.hmd.learnredis.models.RefreshToken;
import com.hmd.learnredis.models.Role;
import com.hmd.learnredis.models.User;
import com.hmd.learnredis.repositories.RoleRepository;
import com.hmd.learnredis.repositories.UserRepository;
import com.hmd.learnredis.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String roleName = request.getRoleName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username or password is incorrect"));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new BadCredentialsException("Username or password is incorrect");
        Role role = roleRepository.findByRoleName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
        if (!user.getRoles().contains(role))
            throw new AccessDeniedException("Access denied");
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return LoginResponse.builder()
                .accessToken(jwtUtils.generateToken(userDetails.getUsername()))
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
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            if (jwtUtils.validateToken(accessToken)) {
                String username = jwtUtils.extractUsername(accessToken);
                userRepository.findByUsername(username).ifPresent(refreshTokenService::deleteByUser);
            }
        }
    }
}
