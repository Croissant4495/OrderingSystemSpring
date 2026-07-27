package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.dto.user.UserRoleDTO;

public interface RoleService {
    List<String> getAllRoles();
    
    String getRoleById(Long id);

    UserResponseDTO updateUserRole(Long userId, UserRoleDTO dto);
}
