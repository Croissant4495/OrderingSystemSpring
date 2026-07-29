package com.ejada.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ejada.project.config.SecurityConfig;
import com.ejada.project.dto.cart.CartItemRequestDTO;
import com.ejada.project.dto.cart.CartItemUpdateDTO;
import com.ejada.project.dto.cart.CartResponseDTO;
import com.ejada.project.security.CustomUserDetailsService;
import com.ejada.project.security.JwtAccessDeniedHandler;
import com.ejada.project.security.JwtAuthenticationEntryPoint;
import com.ejada.project.security.JwtAuthenticationFilter;
import com.ejada.project.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Test
    @WithMockUser
    void getCart_WhenAuthenticated_ShouldReturnOk() throws Exception {
        when(cartService.getCart()).thenReturn(new CartResponseDTO());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk());
    }

    @Test
    void getCart_WhenUnauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void addItemToCart_WhenValidRequest_ShouldReturnCreated() throws Exception {
        CartItemRequestDTO request = new CartItemRequestDTO();
        request.setProductId(1L);
        request.setQuantity(2);

        when(cartService.addItemToCart(any(CartItemRequestDTO.class))).thenReturn(new CartResponseDTO());

        mockMvc.perform(post("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void updateCartItem_WhenValidRequest_ShouldReturnOk() throws Exception {
        CartItemUpdateDTO request = new CartItemUpdateDTO();
        request.setQuantity(5);

        when(cartService.updateCartItem(eq(1L), any(CartItemUpdateDTO.class))).thenReturn(new CartResponseDTO());

        mockMvc.perform(put("/api/cart/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void removeCartItem_ShouldReturnOk() throws Exception {
        when(cartService.removeCartItem(1L)).thenReturn(new CartResponseDTO());

        mockMvc.perform(delete("/api/cart/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void clearCart_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isNoContent());
    }
}
