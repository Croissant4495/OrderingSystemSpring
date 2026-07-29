package com.ejada.project.service.impl;

import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.dto.user.UserRoleDTO;
import com.ejada.project.enums.RoleName;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.RoleMapper;
import com.ejada.project.mapper.UserMapper;
import com.ejada.project.model.Role;
import com.ejada.project.model.User;
import com.ejada.project.repository.RoleRepository;
import com.ejada.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setRoles(new HashSet<>());

        testRole = new Role();
        testRole.setId(2L);
        testRole.setName(RoleName.ADMIN);
    }

    @Test
    void updateUserRole_WhenUserAndRoleExist_ShouldAddRoleAndReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(testRole));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(new UserResponseDTO());

        UserRoleDTO dto = new UserRoleDTO();
        dto.setRoleName(RoleName.ADMIN);

        // Act
        UserResponseDTO result = roleService.updateUserRole(1L, dto);

        // Assert
        assertNotNull(result);
        assertTrue(testUser.getRoles().contains(testRole));
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserRole_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserRoleDTO dto = new UserRoleDTO();
        dto.setRoleName(RoleName.ADMIN);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.updateUserRole(1L, dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserRole_WhenRoleNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.empty());

        UserRoleDTO dto = new UserRoleDTO();
        dto.setRoleName(RoleName.ADMIN);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.updateUserRole(1L, dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoleNames() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(List.of(testRole));
        when(roleMapper.toRoleName(testRole)).thenReturn("ADMIN");

        // Act
        List<String> result = roleService.getAllRoles();

        // Assert
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0));
        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_WhenExists_ShouldReturnRoleName() {
        // Arrange
        when(roleRepository.findById(2L)).thenReturn(Optional.of(testRole));
        when(roleMapper.toRoleName(testRole)).thenReturn("ADMIN");

        // Act
        String result = roleService.getRoleById(2L);

        // Assert
        assertEquals("ADMIN", result);
        verify(roleRepository).findById(2L);
    }

    @Test
    void getRoleById_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(2L));
    }
}
