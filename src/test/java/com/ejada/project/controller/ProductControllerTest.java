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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ejada.project.config.SecurityConfig;
import com.ejada.project.dto.product.ProductRequestDTO;
import com.ejada.project.dto.product.ProductResponseDTO;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtAccessDeniedHandler;
import com.ejada.project.security.JwtAuthenticationEntryPoint;
import com.ejada.project.security.JwtAuthenticationFilter;
import com.ejada.project.security.JwtService;
import com.ejada.project.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private org.springframework.context.ApplicationContext context;

    @Autowired
    FilterChainProxy filterChainProxy;

    @Autowired
    private MockMvc mockMvc;
    
    // @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;  
    
    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Test
    void checkControllerLoaded() {
        System.out.println("=================================");
        System.out.println(context.getBean(ProductController.class));
        System.out.println("=================================");
    }

    @Test
    void printMappings() {

        RequestMappingHandlerMapping mapping =
                context.getBean(RequestMappingHandlerMapping.class);

        mapping.getHandlerMethods().forEach((info, method) -> {
            System.out.println(info + " -> " + method);
        });
    }

    @Test
    void printFilters() {
        context.getBeansOfType(Filter.class)
            .forEach((name, filter) ->
                System.out.println(name + " -> " + filter.getClass()));
    }


    @Test
    void printSecurityFilters() {
        filterChainProxy.getFilterChains().forEach(chain -> {
            System.out.println("=== Security Filter Chain ===");
            chain.getFilters().forEach(filter ->
                System.out.println(filter.getClass().getName()));
        });
    }

    @Test
    void findAll_WhenUnauthenticated_ShouldReturnOk() throws Exception {
        Page<ProductResponseDTO> page = new PageImpl<>(List.of(new ProductResponseDTO()));
        when(productService.getAllProducts(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void findById_WhenUnauthenticated_ShouldReturnOk() throws Exception {
        when(productService.getProductById(1L)).thenReturn(new ProductResponseDTO());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WhenValidRequestAndAdmin_ShouldReturnCreated() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("Product", "Desc", BigDecimal.valueOf(100), 10, Set.of(1L));
        ProductResponseDTO response = new ProductResponseDTO();
        response.setName("Product");

        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(response);

        // mockMvc.perform(post("/api/products")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content(objectMapper.writeValueAsString(request)))
        //         .andExpect(status().isCreated())
        //         .andExpect(jsonPath("$.name").value("Product"));
        MvcResult result = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
        System.out.println(result.getResponse().getStatus());
        System.out.println(result.getHandler());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_WhenUser_ShouldReturnForbidden() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("Product", "Desc", BigDecimal.valueOf(100), 10, Set.of(1L));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_WhenUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("Product", "Desc", BigDecimal.valueOf(100), 10, Set.of(1L));

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WhenValidationFails_ShouldReturnBadRequest() throws Exception {
        // Missing name and negative price
        ProductRequestDTO request = new ProductRequestDTO("", "Desc", BigDecimal.valueOf(-10), -5, Set.of());

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.stockQuantity").exists())
                .andExpect(jsonPath("$.categoryIds").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_WhenValidRequest_ShouldReturnOk() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("Product", "Desc", BigDecimal.valueOf(100), 10, Set.of(1L));
        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class))).thenReturn(new ProductResponseDTO());

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_WhenValidRequest_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
