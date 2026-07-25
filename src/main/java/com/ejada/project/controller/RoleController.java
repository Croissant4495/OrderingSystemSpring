package com.ejada.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.project.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "7. Roles")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<String> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getRoleById(id));
    }
}