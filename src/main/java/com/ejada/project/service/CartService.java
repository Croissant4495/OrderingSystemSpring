package com.ejada.project.service;

import com.ejada.project.dto.cart.CartItemRequestDTO;
import com.ejada.project.dto.cart.CartItemUpdateDTO;
import com.ejada.project.dto.cart.CartResponseDTO;

public interface CartService {
    
    CartResponseDTO getCart();
    
    CartResponseDTO addItemToCart(CartItemRequestDTO dto);
    
    CartResponseDTO updateCartItem(Long cartItemId, CartItemUpdateDTO dto);
    
    CartResponseDTO removeCartItem(Long cartItemId);
    
    void clearCart();
}
