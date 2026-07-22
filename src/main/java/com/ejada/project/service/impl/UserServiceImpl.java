package com.ejada.project.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
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
        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userMapper.updateEntity(dto, user);
        
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
