package com.ejada.project.service;

import java.util.List;

import com.ejada.project.model.Role;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role createRole(Role role);

    void deleteRole(Long id);
}