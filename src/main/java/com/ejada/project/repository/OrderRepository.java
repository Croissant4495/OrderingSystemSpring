package com.ejada.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.project.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    
}
