package com.ejada.project.service;

import com.ejada.project.dto.auth.AuthRequestDTO;
import com.ejada.project.dto.auth.AuthResponseDTO;
import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(UserRequestDTO request);

    AuthResponseDTO login(AuthRequestDTO request);
}
