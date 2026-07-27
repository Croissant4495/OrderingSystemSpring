package com.ejada.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.dto.order.OrderStatusDTO;
import com.ejada.project.enums.OrderStatus;
import com.ejada.project.exception.BadRequestException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.OrderMapper;
import com.ejada.project.model.Cart;
import com.ejada.project.model.CartItem;
import com.ejada.project.model.Order;
import com.ejada.project.model.OrderItem;
import com.ejada.project.model.Product;
import com.ejada.project.model.User;
import com.ejada.project.repository.CartRepository;
import com.ejada.project.repository.OrderRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.repository.UserRepository;
import com.ejada.project.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Order> orders;

        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Authenticated user not found."));

            orders = orderRepository.findByUser(user);
        }

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));

        if (!isAdmin) {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Authenticated user not found."));

            if (!order.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException(
                        "You are not allowed to access this order.");
            }
        }

        return orderMapper.toResponseDTO(order);
    }

    /**
     * Creates a new order. The entire operation is transactional — stock deduction
     * and order persistence succeed or fail together.
     */
    @Override
    @Transactional
    public OrderResponseDTO createOrder() {
        // Verify the user exists
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with username: " + username));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Cart is empty."));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty.");
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // Reject products with no stock (out-of-stock / unavailable)
            if (product.getStockQuantity() <= 0) {
                throw new BadRequestException(
                        "Product '" + product.getName() + "' is out of stock.");
            }

            // Verify sufficient stock exists
            if (product.getStockQuantity() < quantity) {
                throw new BadRequestException(
                        "Insufficient stock for product '" + product.getName() +
                        "'. Requested: " + quantity +
                        ", Available: " + product.getStockQuantity());
            }

            // Copy current price into priceAtPurchase (snapshot at order time)
            BigDecimal priceAtPurchase = product.getPrice();
            BigDecimal subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);

            items.add(buildOrderItem(product, quantity, priceAtPurchase));
        }

        // Build and save the order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(total);

        // Link each item to this order
        for (OrderItem item : items) {
            item.setOrder(order);
        }
        order.setOrderItems(items);

        Order savedOrder = orderRepository.save(order);

        // Clear the cart
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return orderMapper.toResponseDTO(savedOrder);
    }

    /**
     * Updates only the status of an existing order. Order items are immutable.
     */
    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatusDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(dto.getStatus());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private OrderItem buildOrderItem(Product product, int quantity, BigDecimal priceAtPurchase) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPriceAtPurchase(priceAtPurchase);
        return item;
    }
}
