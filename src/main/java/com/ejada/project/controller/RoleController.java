package com.ejada.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getRoleById(id));
    }
}