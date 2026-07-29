package com.ejada.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ejada.project.config.SecurityConfig;
import com.ejada.project.dto.auth.AuthRequestDTO;
import com.ejada.project.dto.auth.AuthResponseDTO;
import com.ejada.project.dto.user.UserRequestDTO;
import com.ejada.project.dto.user.UserResponseDTO;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtAccessDeniedHandler;
import com.ejada.project.security.JwtAuthenticationEntryPoint;
import com.ejada.project.security.JwtAuthenticationFilter;
import com.ejada.project.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Test
    void register_WhenValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("user1", "pass", "u@example.com", "F", "L");
        UserResponseDTO response = new UserResponseDTO();
        response.setUsername("user1");

        when(authService.register(any(UserRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        AuthRequestDTO request = new AuthRequestDTO("user1", "pass");
        AuthResponseDTO response = new AuthResponseDTO("dummy-token");

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }
}
