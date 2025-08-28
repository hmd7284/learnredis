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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    class CreateUserTest {
        @Test
        void testCreateUser_whenUsernameExists_shouldThrowUsernameAlreadyExistsException() {
            CreateUserRequest request = mock(CreateUserRequest.class);

            when(request.getUsername()).thenReturn("existingUser");
            when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

            assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(request));

            verify(userRepository).findByUsername("existingUser");
            verifyNoInteractions(passwordEncoder, roleRepository, userMapper);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void testCreateUser_whenRoleDoesNotExist_shouldThrowRoleNotFoundException() {
            CreateUserRequest request = mock(CreateUserRequest.class);

            when(request.getUsername()).thenReturn("newUser");
            when(request.getRoles()).thenReturn(Set.of("newRole"));
            when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
            when(roleRepository.findByRoleName("newRole")).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> userService.createUser(request));

            verify(userRepository).findByUsername("newUser");
            verify(roleRepository).findByRoleName("newRole");
            verifyNoInteractions(passwordEncoder, userMapper);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void testCreateUser_success() {
            CreateUserRequest request = mock(CreateUserRequest.class);

            when(request.getUsername()).thenReturn("newUser");
            when(request.getPassword()).thenReturn("newPassword");
            when(request.getRoles()).thenReturn(Set.of("newRole"));
            when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());

            Role role = Role.builder()
                    .id(1L)
                    .roleName("newRole")
                    .build();

            when(roleRepository.findByRoleName("newRole")).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            UserDTO userDTO = UserDTO.builder()
                    .id(1L)
                    .build();
            when(userMapper.toUserDTO(any(User.class))).thenReturn(userDTO);

            UserDTO result = userService.createUser(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);

            verify(userRepository).findByUsername("newUser");
            verify(roleRepository).findByRoleName("newRole");
            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUsername()).isEqualTo("newUser");
            assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
            assertThat(capturedUser.getRoles()).contains(role);
            verify(userMapper).toUserDTO(capturedUser);
        }
    }

    @Nested
    class GetUserTest {
        @Test
        void testGetUser_whenIdDoesNotExist_shouldThrowUserNotFoundException() {
            Long id = 1L;
            when(userRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
            verify(userRepository).findById(id);
            verifyNoInteractions(passwordEncoder, roleRepository, userMapper);
        }

        @Test
        void testGetUser_success() {
            Long id = 1L;
            User user = new User();
            user.setId(id);
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            UserDTO userDTO = UserDTO.builder()
                    .id(id)
                    .build();
            when(userMapper.toUserDTO(user)).thenReturn(userDTO);

            UserDTO result = userService.getUserById(id);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            verify(userRepository).findById(id);
            verifyNoInteractions(passwordEncoder, roleRepository);
        }
    }

    @Nested
    class DeleteUserTest {
        @Test
        void testDeleteUser_Success() {
            Long id = 1L;
            userService.deleteUser(id);
            assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));

            verify(userRepository).deleteById(id);
            verifyNoInteractions(passwordEncoder, roleRepository, userMapper);
        }
    }

    @Nested
    class UpdateUserTest {
        @Test
        void testUpdateUser_whenIdDoesNotExist_shouldThrowUserNotFoundException() {
            Long id = 1L;
            UpdateUserRequest request = mock(UpdateUserRequest.class);
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, request));

            verify(userRepository).findById(id);
            verifyNoInteractions(passwordEncoder, roleRepository, userMapper);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void testUpdateUser_whenUsernameAlreadyExists_shouldThrowUserNotFoundException() {
            Long id = 1L;
            UpdateUserRequest request = mock(UpdateUserRequest.class);
            when(request.getUsername()).thenReturn("existingUser");

            User user = User.builder()
                    .id(id)
                    .build();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            User existingUser = User.builder()
                    .id(2L)
                    .build();
            when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));
            assertThrows(UsernameAlreadyExistsException.class, () -> userService.updateUser(id, request));

            verify(userRepository).findById(id);
            verify(userRepository).findByUsername("existingUser");
            verifyNoInteractions(passwordEncoder, roleRepository, userMapper);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void testUpdateUser_whenRoleDoesNotExist_shouldThrowRoleNotFoundException() {
            Long id = 1L;
            UpdateUserRequest request = mock(UpdateUserRequest.class);
            when(request.getRoles()).thenReturn(Set.of("newRole"));
            when(request.getUsername()).thenReturn("validUsername");
            User user = User.builder()
                    .id(id)
                    .build();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.findByUsername("validUsername")).thenReturn(Optional.empty());
            when(roleRepository.findByRoleName("newRole")).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> userService.updateUser(id, request));

            verify(userRepository).findById(id);
            verify(userRepository).findByUsername("validUsername");
            verifyNoInteractions(passwordEncoder, userMapper);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void testUpdateUser_success() {
            Long id = 1L;
            UpdateUserRequest request = mock(UpdateUserRequest.class);
            when(request.getUsername()).thenReturn("newUsername");
            when(request.getPassword()).thenReturn("newPassword");
            when(request.getRoles()).thenReturn(Set.of("newRole"));

            User user = User.builder()
                    .id(id)
                    .build();
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.findByUsername("newUsername")).thenReturn(Optional.of(user));

            Role role = Role.builder()
                    .id(1L)
                    .roleName("newRole")
                    .build();
            when(roleRepository.findByRoleName("newRole")).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            UserDTO userDTO = UserDTO.builder()
                    .id(1L)
                    .build();
            when(userMapper.toUserDTO(any(User.class))).thenReturn(userDTO);
            UserDTO result = userService.updateUser(id, request);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);

            verify(userRepository).findByUsername("newUsername");
            verify(roleRepository).findByRoleName("newRole");
            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUsername()).isEqualTo("newUsername");
            assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
            assertThat(capturedUser.getRoles()).contains(role);
            verify(userMapper).toUserDTO(capturedUser);
        }
    }
}
