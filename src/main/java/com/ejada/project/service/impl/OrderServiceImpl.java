package com.ejada.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejada.project.dto.order.OrderItemRequestDTO;
import com.ejada.project.dto.order.OrderRequestDTO;
import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.dto.order.OrderStatusDTO;
import com.ejada.project.enums.OrderStatus;
import com.ejada.project.exception.BadRequestException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.OrderMapper;
import com.ejada.project.model.Order;
import com.ejada.project.model.OrderItem;
import com.ejada.project.model.Product;
import com.ejada.project.model.User;
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
    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponseDTO(order);
    }

    /**
     * Creates a new order. The entire operation is transactional — stock deduction
     * and order persistence succeed or fail together.
     */
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        // Verify the user exists
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with username: " + username));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            // Quantity must be greater than zero (belt-and-suspenders beyond DTO annotation)
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new BadRequestException("Item quantity must be greater than zero.");
            }

            // Verify the product exists
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemDto.getProductId()));

            // Reject products with no stock (out-of-stock / unavailable)
            if (product.getStockQuantity() <= 0) {
                throw new BadRequestException(
                        "Product '" + product.getName() + "' is out of stock.");
            }

            // Verify sufficient stock exists
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for product '" + product.getName() +
                        "'. Requested: " + itemDto.getQuantity() +
                        ", Available: " + product.getStockQuantity());
            }

            // Copy current price into priceAtPurchase (snapshot at order time)
            BigDecimal priceAtPurchase = product.getPrice();
            BigDecimal subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            total = total.add(subtotal);

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            items.add(buildOrderItem(product, itemDto.getQuantity(), priceAtPurchase));
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
