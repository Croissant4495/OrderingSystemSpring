package com.ejada.project.dto.user;

import com.ejada.project.enums.RoleName;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {

    @NotNull(message = "Role name is required")
    private RoleName roleName;
}
