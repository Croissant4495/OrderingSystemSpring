package com.ejada.project.service.impl;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejada.project.dto.cart.CartItemRequestDTO;
import com.ejada.project.dto.cart.CartItemUpdateDTO;
import com.ejada.project.dto.cart.CartResponseDTO;
import com.ejada.project.exception.BadRequestException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.CartMapper;
import com.ejada.project.model.Cart;
import com.ejada.project.model.CartItem;
import com.ejada.project.model.Product;
import com.ejada.project.model.User;
import com.ejada.project.repository.CartItemRepository;
import com.ejada.project.repository.CartRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.repository.UserRepository;
import com.ejada.project.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCart() {
        Cart cart = getOrCreateCartForAuthenticatedUser();
        return cartMapper.toResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO addItemToCart(CartItemRequestDTO dto) {
        if (dto.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateCartForAuthenticatedUser();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(dto.getQuantity());
            cart.getCartItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDTO updateCartItem(Long cartItemId, CartItemUpdateDTO dto) {
        Cart cart = getOrCreateCartForAuthenticatedUser();

        CartItem itemToUpdate = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + cartItemId + " in your cart"));

        if (dto.getQuantity() == 0) {
            cart.getCartItems().remove(itemToUpdate);
        } else {
            itemToUpdate.setQuantity(dto.getQuantity());
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(savedCart);
    }

    @Override
    @Transactional
    public CartResponseDTO removeCartItem(Long cartItemId) {
        Cart cart = getOrCreateCartForAuthenticatedUser();

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + cartItemId + " in your cart"));

        cart.getCartItems().remove(itemToRemove);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(savedCart);
    }

    @Override
    @Transactional
    public void clearCart() {
        Cart cart = getOrCreateCartForAuthenticatedUser();
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCartForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }
}
