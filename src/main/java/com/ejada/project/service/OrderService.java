package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.order.OrderRequestDTO;
import com.ejada.project.dto.order.OrderResponseDTO;

public interface OrderService {

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Long id);

    OrderResponseDTO createOrder(OrderRequestDTO dto);

    OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto);

    void deleteOrder(Long id);
}