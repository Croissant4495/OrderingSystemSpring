package com.ejada.project.service.impl;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.dto.user.UserUpdateRequestDTO;
import com.ejada.project.enums.RoleName;
import com.ejada.project.exception.ResourceAlreadyExistsException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.RoleMapper;
import com.ejada.project.mapper.UserMapper;
import com.ejada.project.model.Role;
import com.ejada.project.model.User;
import com.ejada.project.repository.RoleRepository;
import com.ejada.project.repository.UserRepository;
import com.ejada.project.util.TestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testUser = TestBuilder.createUser(1L, "user1", "user1@example.com", RoleName.USER);
        testAdmin = TestBuilder.createUser(2L, "admin", "admin@example.com", RoleName.ADMIN);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(String username, String role) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(username);
        doReturn(List.of(new SimpleGrantedAuthority(role))).when(authentication).getAuthorities();
    }

    @Test
    void getUsers_WhenAdmin_ShouldReturnAllUsers() {
        // Arrange
        mockSecurityContext("admin", "ROLE_ADMIN");
        when(userRepository.findAll()).thenReturn(List.of(testUser, testAdmin));
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        // Act
        List<UserResponseDTO> users = userService.getUsers();

        // Assert
        assertEquals(2, users.size());
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUsers_WhenUser_ShouldReturnOnlySelf() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(testUser)).thenReturn(new UserResponseDTO());

        // Act
        List<UserResponseDTO> users = userService.getUsers();

        // Assert
        assertEquals(1, users.size());
        verify(userRepository).findByUsername("user1");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(testUser)).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO response = userService.getUserById(1L);

        // Assert
        assertNotNull(response);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_WhenValidRequest_ShouldCreateAndReturnUser() {
        // Arrange
        UserRequestDTO request = TestBuilder.createUserRequestDTO("newuser", "newuser@example.com");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        User mappedUser = new User();
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        
        Role userRole = new Role(1L, RoleName.USER, null);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
        
        when(userRepository.save(any(User.class))).thenReturn(mappedUser);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
        assertEquals("encodedPassword", mappedUser.getPassword());
        assertTrue(mappedUser.getRoles().contains(userRole));
    }

    @Test
    void createUser_WhenUsernameExists_ShouldThrowException() {
        // Arrange
        UserRequestDTO request = TestBuilder.createUserRequestDTO("newuser", "newuser@example.com");
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        // Arrange
        UserRequestDTO request = TestBuilder.createUserRequestDTO("newuser", "newuser@example.com");
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserUpdatesOwnProfile_ShouldUpdateAndReturnUser() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        UserUpdateRequestDTO request = TestBuilder.createUserUpdateRequestDTO("user1_updated", "user1_updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        
        doNothing().when(userMapper).updateEntity(request, testUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO response = userService.updateUser(1L, request);

        // Assert
        assertNotNull(response);
        verify(userRepository).save(testUser);
        assertEquals("encodedNewPassword", testUser.getPassword());
    }

    @Test
    void updateUser_WhenUserUpdatesAnotherProfile_ShouldThrowAccessDeniedException() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        UserUpdateRequestDTO request = TestBuilder.createUserUpdateRequestDTO("updated", "updated@example.com");

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(2L, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WhenDuplicateUsername_ShouldThrowException() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        
        UserUpdateRequestDTO request = TestBuilder.createUserUpdateRequestDTO("admin", "admin_new@example.com");
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(testAdmin));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_WhenExists_ShouldDelete() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
