package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.requests.LoginRequest;
import com.hmd.learnredis.exceptions.RoleNotFoundException;
import com.hmd.learnredis.models.Role;
import com.hmd.learnredis.models.User;
import com.hmd.learnredis.repositories.RoleRepository;
import com.hmd.learnredis.repositories.UserRepository;
import com.hmd.learnredis.utils.JwtUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private AuthService authService;

    @Nested
    class TestLogin {
        @Test
        void testLogin_whenUsernameDoesNotExist_shouldThrowUsernameNotFoundException() {
            LoginRequest loginRequest = mock(LoginRequest.class);
            when(loginRequest.getUsername()).thenReturn("username");
            when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
            assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest));
            verify(userRepository).findByUsername("username");
        }

        @Test
        void testLogin_whenPasswordIsIncorrect_shouldThrowBadCredentialsException() {
            LoginRequest loginRequest = mock(LoginRequest.class);
            when(loginRequest.getUsername()).thenReturn("username");
            when(loginRequest.getPassword()).thenReturn("password");

            User user = User.builder()
                    .username("username")
                    .password("encodedPassword")
                    .build();
            when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);
            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        }

        @Test
        void testLogin_whenRoleDoesNotExist_shouldThrowRoleNotFoundException() {
            LoginRequest loginRequest = mock(LoginRequest.class);
            when(loginRequest.getUsername()).thenReturn("username");
            when(loginRequest.getPassword()).thenReturn("password");
            when(loginRequest.getRoleName()).thenReturn("ADMIN");

            User user = User.builder()
                    .username("username")
                    .password("encodedPassword")
                    .build();
            when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
            when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.empty());
            assertThrows(RoleNotFoundException.class, () -> authService.login(loginRequest));
        }

        @Test
        void testLogin_whenUserDoesNotHaveRole_shouldThrowAccessDeniedException() {
            LoginRequest loginRequest = mock(LoginRequest.class);
            when(loginRequest.getUsername()).thenReturn("username");
            when(loginRequest.getPassword()).thenReturn("password");
            when(loginRequest.getRoleName()).thenReturn("ADMIN");
            User user = User.builder()
                    .username("username")
                    .password("encodedPassword")
                    .roles(Set.of(Role.builder()
                            .roleName("USER")
                            .build()))
                    .build();
            Role role = Role.builder()
                    .roleName("ADMIN")
                    .build();
            when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
            when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(role));
            assertThrows(AccessDeniedException.class, () -> authService.login(loginRequest));

            verify(userRepository).findByUsername("username");
            verify(passwordEncoder).matches("password", "encodedPassword");
            verify(roleRepository).findByRoleName("ADMIN");
        }
    }
}
