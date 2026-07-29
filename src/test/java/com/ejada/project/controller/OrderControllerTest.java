package com.ejada.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.dto.order.OrderStatusDTO;
import com.ejada.project.enums.OrderStatus;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtAccessDeniedHandler;
import com.ejada.project.security.JwtAuthenticationEntryPoint;
import com.ejada.project.security.JwtAuthenticationFilter;
import com.ejada.project.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Test
    @WithMockUser(roles = "USER")
    void getAllOrders_WhenUser_ShouldReturnOk() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(new OrderResponseDTO()));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllOrders_WhenUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrderById_WhenUser_ShouldReturnOk() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(new OrderResponseDTO());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_WhenUser_ShouldReturnCreated() throws Exception {
        when(orderService.createOrder()).thenReturn(new OrderResponseDTO());

        mockMvc.perform(post("/api/orders"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_WhenAdmin_ShouldReturnOk() throws Exception {
        OrderStatusDTO request = new OrderStatusDTO();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderService.updateOrderStatus(eq(1L), any(OrderStatusDTO.class))).thenReturn(new OrderResponseDTO());

        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateOrderStatus_WhenUser_ShouldReturnForbidden() throws Exception {
        OrderStatusDTO request = new OrderStatusDTO();
        request.setStatus(OrderStatus.SHIPPED);

        mockMvc.perform(put("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_WhenAdmin_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteOrder_WhenUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isForbidden());
    }
}
