package com.ejada.project.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ejada.project.config.SecurityConfig;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtAccessDeniedHandler;
import com.ejada.project.security.JwtAuthenticationEntryPoint;
import com.ejada.project.security.JwtAuthenticationFilter;
import com.ejada.project.service.RoleService;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRoles_WhenAdmin_ShouldReturnOk() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of("ADMIN", "USER"));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRoles_WhenUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllRoles_WhenUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoleById_WhenAdmin_ShouldReturnOk() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn("ADMIN");

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRoleById_WhenUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRoleById_WhenUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isUnauthorized());
    }
}
