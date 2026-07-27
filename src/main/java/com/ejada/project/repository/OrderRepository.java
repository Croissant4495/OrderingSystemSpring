package com.ejada.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.project.model.Order;
import com.ejada.project.model.User;

public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUser(User user);
}
