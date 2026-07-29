package com.ejada.project.util;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.dto.user.UserUpdateRequestDTO;
import com.ejada.project.enums.RoleName;
import com.ejada.project.model.Role;
import com.ejada.project.model.User;

import java.util.HashSet;
import java.util.Set;

public class TestBuilder {

    public static User createUser(Long id, String username, String email, RoleName roleName) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(email);
        user.setFirstName("First");
        user.setLastName("Last");
        
        Role role = new Role();
        role.setId(roleName == RoleName.ADMIN ? 2L : 1L);
        role.setName(roleName);
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        
        return user;
    }

    public static UserRequestDTO createUserRequestDTO(String username, String email) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword("password");
        dto.setFirstName("First");
        dto.setLastName("Last");
        return dto;
    }

    public static UserUpdateRequestDTO createUserUpdateRequestDTO(String username, String email) {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword("newpassword");
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");
        return dto;
    }

    public static UserResponseDTO createUserResponseDTO(Long id, String username, String email, RoleName roleName) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFirstName("First");
        dto.setLastName("Last");
        
        Set<String> roles = new HashSet<>();
        roles.add(roleName.name());
        dto.setRoles(roles);
        
        return dto;
    }
}
