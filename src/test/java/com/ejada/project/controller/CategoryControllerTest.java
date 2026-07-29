package com.ejada.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ejada.project.config.SecurityConfig;
import com.ejada.project.dto.category.CategoryRequestDTO;
import com.ejada.project.dto.category.CategoryResponseDTO;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtAccessDeniedHandler;
import com.ejada.project.security.JwtAuthenticationEntryPoint;
import com.ejada.project.security.JwtAuthenticationFilter;
import com.ejada.project.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Test
    void getAllCategories_WhenUnauthenticated_ShouldReturnOk() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(new CategoryResponseDTO()));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryById_WhenUnauthenticated_ShouldReturnOk() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(new CategoryResponseDTO());

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_WhenValidRequestAndAdmin_ShouldReturnCreated() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Electronics");
        
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setName("Electronics");

        when(categoryService.createCategory(any(CategoryRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCategory_WhenUser_ShouldReturnForbidden() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Electronics");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_WhenValidRequestAndAdmin_ShouldReturnOk() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Electronics");

        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDTO.class))).thenReturn(new CategoryResponseDTO());

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_WhenAdmin_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}
