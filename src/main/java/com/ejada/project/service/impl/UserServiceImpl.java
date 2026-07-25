package com.ejada.project.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.dto.user.UserRoleDTO;
import com.ejada.project.enums.RoleName;
import com.ejada.project.exception.ResourceAlreadyExistsException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.RoleMapper;
import com.ejada.project.mapper.UserMapper;
import com.ejada.project.model.Role;
import com.ejada.project.model.User;
import com.ejada.project.repository.RoleRepository;
import com.ejada.project.repository.UserRepository;
import com.ejada.project.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public List<UserResponseDTO> getUsers() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return userRepository.findAll().stream()
                    .map(userMapper::toResponseDTO)
                    .toList();
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found."));

        return List.of(userMapper.toResponseDTO(user));
    }
    
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        // Uniqueness checks
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Username already taken: " + dto.getUsername());
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException(
                    "Email already registered: " + dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Auto-assign the default ROLE_USER
        Role defaultRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Default role ROLE_USER not found. Please seed the roles table."));
        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found."));

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException(
                    "You may only update your own profile.");
        }

        // If username is being changed, ensure it's not taken by another user
        userRepository.findByUsername(dto.getUsername()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResourceAlreadyExistsException(
                        "Username already taken: " + dto.getUsername());
            }
        });

        // If email is being changed, ensure it's not taken by another user
        userRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResourceAlreadyExistsException(
                        "Email already registered: " + dto.getEmail());
            }
        });

        userMapper.updateEntity(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO updateUserRole(Long userId, UserRoleDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found: " + dto.getRoleName()));

        // Additive: add role to existing set (no-op if already present)
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleName)
                .collect(Collectors.toList());
    }

    @Override
    public String getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return roleMapper.toRoleName(role);
    }
}
