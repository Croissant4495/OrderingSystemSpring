package com.ejada.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.project.dto.cart.CartItemRequestDTO;
import com.ejada.project.dto.cart.CartItemUpdateDTO;
import com.ejada.project.dto.cart.CartResponseDTO;
import com.ejada.project.service.CartService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "3. Shopping Cart")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItemToCart(@Valid @RequestBody CartItemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItemToCart(dto));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateDTO dto) {
        return ResponseEntity.ok(cartService.updateCartItem(itemId, dto));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeCartItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeCartItem(itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
