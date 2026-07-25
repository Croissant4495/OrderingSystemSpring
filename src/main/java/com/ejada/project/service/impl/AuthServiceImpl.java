package com.ejada.project.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ejada.project.dto.auth.AuthRequestDTO;
import com.ejada.project.dto.auth.AuthResponseDTO;
import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtService;
import com.ejada.project.service.AuthService;
import com.ejada.project.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;

    public UserResponseDTO register(UserRequestDTO dto) {
        return userService.createUser(dto);
    }
    
    public AuthResponseDTO login(AuthRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token);
    }
}
