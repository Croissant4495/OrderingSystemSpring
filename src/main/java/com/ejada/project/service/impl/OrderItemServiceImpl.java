package com.ejada.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ejada.project.model.OrderItem;
import com.ejada.project.repository.OrderItemRepository;
import com.ejada.project.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id).orElseThrow();
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }
}
