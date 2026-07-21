package com.ejada.project.service;

import java.util.List;

import com.ejada.project.model.OrderItem;

public interface OrderItemService {

    List<OrderItem> getAllOrderItems();

    OrderItem getOrderItemById(Long id);

    OrderItem createOrderItem(OrderItem orderItem);

    void deleteOrderItem(Long id);
}