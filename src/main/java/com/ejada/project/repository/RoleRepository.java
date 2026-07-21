package com.ejada.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.project.enums.RoleName;
import com.ejada.project.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
