package com.ejada.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ejada.project.dto.order.OrderItemRequestDTO;
import com.ejada.project.dto.order.OrderRequestDTO;
import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.enums.OrderStatus;
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

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        Order order = orderMapper.toEntity(dto);
        
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
        order.setUser(user);
        
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
            
            items.add(createOrderItem(order, product, itemDto));
            
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }
        
        order.setOrderItems(items);
        order.setTotalAmount(total);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        orderMapper.updateEntity(dto, order);
        
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
        order.setUser(user);
        
        // Using clear() requires CascadeType.ALL and orphanRemoval=true, which is standard
        // But for simplicity of remapping, we will clear and rebuild
        if (order.getOrderItems() != null) {
            order.getOrderItems().clear();
        } else {
            order.setOrderItems(new ArrayList<>());
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (OrderItemRequestDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
            
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            item.setOrder(order);
            order.getOrderItems().add(item);
            
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }
        
        order.setTotalAmount(total);
        
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

    private OrderItem createOrderItem(
            Order order,
            Product product,
            OrderItemRequestDTO dto) {

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPriceAtPurchase(product.getPrice());

        return item;
    }
}
