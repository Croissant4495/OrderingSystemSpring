package com.ejada.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.enums.OrderStatus;
import com.ejada.project.exception.BadRequestException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.OrderMapper;
import com.ejada.project.model.Cart;
import com.ejada.project.model.CartItem;
import com.ejada.project.model.Order;
import com.ejada.project.model.Product;
import com.ejada.project.model.User;
import com.ejada.project.repository.CartRepository;
import com.ejada.project.repository.OrderRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private User anotherUser;
    private Order testOrder;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("user2");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(BigDecimal.valueOf(200.0));
        testOrder.setOrderDate(LocalDateTime.now());
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setStockQuantity(10);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setCartItems(new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(String username, String role) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(username);
        lenient().doReturn(List.of(new SimpleGrantedAuthority(role))).when(authentication).getAuthorities();
    }

    @Test
    void getAllOrders_WhenAdmin_ShouldReturnAllOrders() {
        // Arrange
        mockSecurityContext("admin", "ROLE_ADMIN");
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(new OrderResponseDTO());

        // Act
        List<OrderResponseDTO> result = orderService.getAllOrders();

        // Assert
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrders_WhenUser_ShouldReturnOnlyOwnOrders() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser(testUser)).thenReturn(List.of(testOrder));
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(new OrderResponseDTO());

        // Act
        List<OrderResponseDTO> result = orderService.getAllOrders();

        // Assert
        assertEquals(1, result.size());
        verify(orderRepository).findByUser(testUser);
    }

    @Test
    void getOrderById_WhenUserOwnsOrder_ShouldReturnOrder() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(orderMapper.toResponseDTO(testOrder)).thenReturn(new OrderResponseDTO());

        // Act
        OrderResponseDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_WhenAdminRequestsAnyOrder_ShouldReturnOrder() {
        // Arrange
        mockSecurityContext("admin", "ROLE_ADMIN");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toResponseDTO(testOrder)).thenReturn(new OrderResponseDTO());

        // Act
        OrderResponseDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        // Admin doesn't need to look up their own user entity for permission check
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void getOrderById_WhenUserRequestsAnotherUsersOrder_ShouldThrowException() {
        // Arrange
        mockSecurityContext("user2", "ROLE_USER");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder)); // Belongs to user1
        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(anotherUser));

        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        // Arrange
        mockSecurityContext("admin", "ROLE_ADMIN");
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void createOrder_WhenSuccessfulCheckout_ShouldCreateOrderAndClearCart() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(cartRepository.save(testCart)).thenReturn(testCart);
        when(orderMapper.toResponseDTO(any(Order.class))).thenReturn(new OrderResponseDTO());

        // Act
        OrderResponseDTO result = orderService.createOrder();

        // Assert
        assertNotNull(result);
        assertEquals(8, testProduct.getStockQuantity()); // 10 - 2
        assertTrue(testCart.getCartItems().isEmpty());
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(testProduct);
        verify(cartRepository).save(testCart);
    }

    @Test
    void createOrder_WhenInsufficientStock_ShouldThrowException() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(20); // More than available stock (10)
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> orderService.createOrder());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WhenZeroStock_ShouldThrowException() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        testProduct.setStockQuantity(0);
        
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);
        cartItem.setCart(testCart);
        testCart.getCartItems().add(cartItem);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> orderService.createOrder());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WhenEmptyCart_ShouldThrowException() {
        // Arrange
        mockSecurityContext("user1", "ROLE_USER");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> orderService.createOrder());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void deleteOrder_WhenExists_ShouldDelete() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_WhenDoesNotExist_ShouldThrowException() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, never()).deleteById(anyLong());
    }
}
