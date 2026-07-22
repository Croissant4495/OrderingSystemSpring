package com.ejada.project.mapper;

import org.mapstruct.Mapper;

import com.ejada.project.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default String toRoleName(Role role) {
        return role == null ? null : role.getName().name();
    }
}
