package com.ejada.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.project.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    
}
