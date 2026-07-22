package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;

public interface UserService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO updateUser(Long id, UserRequestDTO dto);

    void deleteUser(Long id);

    List<String> getAllRoles();

    String getRoleById(Long id);
}
