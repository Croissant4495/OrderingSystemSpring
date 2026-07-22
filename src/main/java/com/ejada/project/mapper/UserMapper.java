package com.ejada.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.model.User;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntity(UserRequestDTO dto, @MappingTarget User user);
}