package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.order.OrderRequestDTO;
import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.dto.order.OrderStatusDTO;

public interface OrderService {

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Long id);

    OrderResponseDTO createOrder(OrderRequestDTO dto);

    OrderResponseDTO updateOrderStatus(Long id, OrderStatusDTO dto);

    void deleteOrder(Long id);
}