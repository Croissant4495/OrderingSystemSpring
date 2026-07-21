package com.ejada.project.service;

import java.util.List;

import com.ejada.project.model.Order;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order createOrder(Order order);

    void deleteOrder(Long id);
}