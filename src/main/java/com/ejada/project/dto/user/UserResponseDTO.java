package com.ejada.project.dto.user;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> roles;
}