package com.ejada.project.service.impl;

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
import com.ejada.project.repository.CartRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setCartItems(new ArrayList<>());

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getCart_WhenCartExists_ShouldReturnCart() {
        // Arrange
        mockSecurityContext();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartMapper.toResponseDTO(testCart)).thenReturn(new CartResponseDTO());

        // Act
        CartResponseDTO result = cartService.getCart();

        // Assert
        assertNotNull(result);
        verify(cartRepository).findByUserId(1L);
    }

    @Test
    void getCart_WhenCartDoesNotExist_ShouldCreateAndReturnCart() {
        // Arrange
        mockSecurityContext();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(new CartResponseDTO());

        // Act
        CartResponseDTO result = cartService.getCart();

        // Assert
        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_WhenValidRequest_ShouldAddItemAndReturnCart() {
        // Arrange
        mockSecurityContext();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(testCart)).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(new CartResponseDTO());

        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(1L);
        dto.setQuantity(2);

        // Act
        CartResponseDTO result = cartService.addItemToCart(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, testCart.getCartItems().size());
        verify(cartRepository).save(testCart);
    }

    @Test
    void addItemToCart_WhenQuantityZero_ShouldThrowException() {
        // Arrange
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(1L);
        dto.setQuantity(0);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> cartService.addItemToCart(dto));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_WhenItemExists_ShouldUpdateQuantity() {
        // Arrange
        mockSecurityContext();
        
        CartItem cartItem = new CartItem();
        cartItem.setId(10L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(new CartResponseDTO());

        CartItemUpdateDTO dto = new CartItemUpdateDTO();
        dto.setQuantity(5);

        // Act
        CartResponseDTO result = cartService.updateCartItem(10L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(5, testCart.getCartItems().get(0).getQuantity());
        verify(cartRepository).save(testCart);
    }

    @Test
    void updateCartItem_WhenQuantityZero_ShouldRemoveItem() {
        // Arrange
        mockSecurityContext();
        
        CartItem cartItem = new CartItem();
        cartItem.setId(10L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(new CartResponseDTO());

        CartItemUpdateDTO dto = new CartItemUpdateDTO();
        dto.setQuantity(0);

        // Act
        CartResponseDTO result = cartService.updateCartItem(10L, dto);

        // Assert
        assertNotNull(result);
        assertTrue(testCart.getCartItems().isEmpty());
        verify(cartRepository).save(testCart);
    }

    @Test
    void updateCartItem_WhenItemDoesNotExist_ShouldThrowException() {
        // Arrange
        mockSecurityContext();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        
        CartItemUpdateDTO dto = new CartItemUpdateDTO();
        dto.setQuantity(5);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.updateCartItem(10L, dto));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeCartItem_WhenItemExists_ShouldRemoveItem() {
        // Arrange
        mockSecurityContext();
        
        CartItem cartItem = new CartItem();
        cartItem.setId(10L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);
        when(cartMapper.toResponseDTO(testCart)).thenReturn(new CartResponseDTO());

        // Act
        CartResponseDTO result = cartService.removeCartItem(10L);

        // Assert
        assertNotNull(result);
        assertTrue(testCart.getCartItems().isEmpty());
        verify(cartRepository).save(testCart);
    }

    @Test
    void clearCart_ShouldRemoveAllItems() {
        // Arrange
        mockSecurityContext();
        
        CartItem cartItem = new CartItem();
        cartItem.setId(10L);
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // Act
        cartService.clearCart();

        // Assert
        assertTrue(testCart.getCartItems().isEmpty());
        verify(cartRepository).save(testCart);
    }
}
