package com.ejada.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.project.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
    
}
