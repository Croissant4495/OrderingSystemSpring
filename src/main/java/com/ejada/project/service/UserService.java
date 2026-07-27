package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.dto.user.UserUpdateRequestDTO;

public interface UserService {

    List<UserResponseDTO> getUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto);

    void deleteUser(Long id);
}
